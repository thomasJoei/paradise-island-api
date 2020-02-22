package com.upgrade.paradise.island.api.services;

import com.upgrade.paradise.island.api.dto.Availability;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<Availability> getAvailabilities(LocalDate startDate, LocalDate endDate);
}
