package com.upgrade.paradise.island.api.db.dao;

import com.upgrade.paradise.island.api.db.model.ReservedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservedDayRepository extends JpaRepository<ReservedDay, LocalDate> {

    @Query(value = "FROM ReservedDay WHERE date >= ?1 AND date < ?2")
    List<ReservedDay> getReservedDayByDateBetween(LocalDate startDate, LocalDate endDate);

}
