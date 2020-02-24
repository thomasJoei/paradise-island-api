package com.upgrade.paradise.island.api.controllers;


import com.upgrade.paradise.island.api.AvailabilitiesApi;
import com.upgrade.paradise.island.api.dto.Availability;
import com.upgrade.paradise.island.api.exceptions.ValidationException;
import com.upgrade.paradise.island.api.services.AvailabilityService;
import com.upgrade.paradise.island.api.services.DateTimeProvider;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;

@Controller
@Api(tags = {"availabilities"})
public class AvailabilitiesController implements AvailabilitiesApi {

    private DateTimeProvider dateTimeProvider;
    private AvailabilityService availabilityService;

    public AvailabilitiesController(@Autowired DateTimeProvider dateTimeProvider, @Autowired AvailabilityService availabilityService) {
        this.dateTimeProvider = dateTimeProvider;
        this.availabilityService = availabilityService;
    }

    @Override
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<Availability>> availabilitiesGet(
        @RequestParam(value = "startDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate) {
        startDate = startDate != null ? startDate : dateTimeProvider.tomorrow();
        endDate = endDate != null ? endDate : startDate.plusMonths(1);

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date must be before end date.");
        }

        return ResponseEntity.ok(availabilityService.getAvailabilities(startDate, endDate));
    }
}
