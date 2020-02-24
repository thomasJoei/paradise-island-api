package com.upgrade.paradise.island.api.db.dao;

import com.upgrade.paradise.island.api.db.model.ReservedDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservedDayRepository extends JpaRepository<ReservedDay, LocalDate> {

    List<ReservedDay> getReservedDaysByDateBetween(LocalDate startDate, LocalDate endDate);

}
