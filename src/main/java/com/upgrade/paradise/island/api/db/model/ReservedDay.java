package com.upgrade.paradise.island.api.db.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reserved_day")
public class ReservedDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Version
    private long version = 0L;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ReservedDay id(int id) {
        this.id = id;
        return this;
    }

    public ReservedDay date(LocalDate date) {
        this.date = date;
        return this;
    }

    public ReservedDay reservation(Reservation reservation) {
        this.reservation = reservation;
        return this;
    }

    public ReservedDay version(long version) {
        this.version = version;
        return this;
    }
}
