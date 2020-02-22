package com.upgrade.paradise.island.api.controllers.validators;

import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.dto.ReservationFieldsDto;
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

    public void validate(ReservationFieldsDto reservationFieldsDto) {

        if (StringUtils.isBlank(reservationFieldsDto.getEmail())) {
            throw new ValidationException("Email required.");
        }
        if (StringUtils.isBlank(reservationFieldsDto.getFullname())) {
            throw new ValidationException("Fullname required.");
        }
        if (reservationFieldsDto.getStartDate() == null) {
            throw new ValidationException("Start date required.");
        }
        if (reservationFieldsDto.getEndDate() == null) {
            throw new ValidationException("End date required.");
        }
        if (reservationFieldsDto.getEndDate().isBefore(reservationFieldsDto.getStartDate())) {
            throw new ValidationException("Start date must be before end date.");
        }
        if (DAYS.between(reservationFieldsDto.getStartDate(),reservationFieldsDto.getEndDate()) > 3) {
            throw new ValidationException("Reservation exceed 3 days.");
        }
        if (dateTimeProvider.now().isAfter(reservationFieldsDto.getStartDate().atTime(12, 0).minusDays(1))
        || reservationFieldsDto.getEndDate().isAfter(dateTimeProvider.nextMonth())) {
            throw new ValidationException("The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.");
        }
    }
}
