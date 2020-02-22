package com.upgrade.paradise.island.api.db.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "email")
    private String email;


    @OneToMany(mappedBy = "reservation",
//        cascade = CascadeType.PERSIST,
//        cascade = CascadeType.ALL,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true)
    private List<ReservedDay> reservedDays = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ReservedDay> getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(List<ReservedDay> reservedDays) {
        updateReservedDays(reservedDays);
    }


    public Reservation id(Integer id) {
        this.id = id;
        return this;
    }

    public Reservation fullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    public Reservation email(String email) {
        this.email = email;
        return this;
    }

    public Reservation reservedDays(List<ReservedDay> reservedDays) {
        updateReservedDays(reservedDays);
        return this;
    }

    private void updateReservedDays(List<ReservedDay> newReservedDays) {
        List<ReservedDay> oldReservedDays =  new ArrayList<>(this.reservedDays);

        Map<LocalDate, ReservedDay> reservedDaysMap = newReservedDays
            .stream()
            .collect(Collectors.toMap(ReservedDay::getDate, d -> d.reservation(this)));

        oldReservedDays.forEach(d -> {
            if (reservedDaysMap.containsKey(d.getDate())) {
                reservedDaysMap.remove(d.getDate());
            } else {
                this.reservedDays.remove(d);
            }
        });

        // add the rest
        this.reservedDays.addAll(reservedDaysMap.values());

    }
}
