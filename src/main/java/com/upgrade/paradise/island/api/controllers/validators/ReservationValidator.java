package com.upgrade.paradise.island.api.controllers.validators;

import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.exceptions.ValidationException;
import com.upgrade.paradise.island.api.services.DateTimeProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class ReservationValidator {

    private DateTimeProvider dateTimeProvider;

    public ReservationValidator(@Autowired DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    public void validate(NewReservationDto newReservationDto) {

        if (StringUtils.isBlank(newReservationDto.getEmail())) {
            throw new ValidationException("Email required.");
        }
        if (StringUtils.isBlank(newReservationDto.getFullname())) {
            throw new ValidationException("Fullname required.");
        }
        if (newReservationDto.getStartDate() == null) {
            throw new ValidationException("Start date required.");
        }
        if (newReservationDto.getEndDate() == null) {
            throw new ValidationException("End date required.");
        }
        if (newReservationDto.getEndDate().isBefore(newReservationDto.getStartDate())) {
            throw new ValidationException("Start date must be before end date.");
        }
        if (DAYS.between(newReservationDto.getStartDate(), newReservationDto.getEndDate()) > 3) {
            throw new ValidationException("Reservation exceed 3 days.");
        }
        if (dateTimeProvider.now().isAfter(newReservationDto.getStartDate().atTime(12, 0).minusDays(1))
            || newReservationDto.getEndDate().isAfter(dateTimeProvider.nextMonth())) {
            throw new ValidationException("The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.");
        }
    }
}
