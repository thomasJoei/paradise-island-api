package com.upgrade.paradise.island.api.services.impl;

import com.upgrade.paradise.island.api.db.dao.ReservedDayRepository;
import com.upgrade.paradise.island.api.db.model.ReservedDay;
import com.upgrade.paradise.island.api.dto.Availability;
import com.upgrade.paradise.island.api.services.AvailabilityService;
import com.upgrade.paradise.island.api.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private ReservedDayRepository reservedDayRepository;

    public AvailabilityServiceImpl(@Autowired ReservedDayRepository reservedDayRepository) {
        this.reservedDayRepository = reservedDayRepository;
    }

    @Override
    public List<Availability> getAvailabilities(LocalDate startDate, LocalDate endDate) {
        List<ReservedDay> reservedDays = reservedDayRepository.getReservedDayByDateBetween(startDate, endDate);

        Map<LocalDate, ReservedDay> reservedDaysMap = reservedDays.stream()
            .collect(Collectors.toMap(ReservedDay::getDate, d -> d));

        return DateUtils.getDatesStream(startDate, endDate)
            .map(date -> new Availability()
                .date(date)
                .isAvailable(!reservedDaysMap.containsKey(date))
            )
            .collect(Collectors.toList());
    }


}
