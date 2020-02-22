package com.upgrade.paradise.island.api.db.dao;

import com.upgrade.paradise.island.api.db.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

}
