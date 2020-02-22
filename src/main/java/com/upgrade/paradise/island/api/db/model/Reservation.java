package com.upgrade.paradise.island.api.db.model;

import javax.persistence.*;
import java.util.List;

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
    private List<ReservedDay> reservedDays;

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
        this.reservedDays = reservedDays;
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
        this.reservedDays = reservedDays;
        return this;
    }
}
