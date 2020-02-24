package com.upgrade.paradise.island.api.controllers.validators;

import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.exceptions.ValidationException;
import com.upgrade.paradise.island.api.services.DateTimeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ReservationValidatorTest {

    @Mock
    private DateTimeProvider dateTimeProvider;

    private ReservationValidator reservationValidator;


    private static final String FULLNAME = "fullname";
    private static final String EMAIL = "email";

    private static final LocalDate TODAY = LocalDate.of(2020, 3, 3);

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        reservationValidator = new ReservationValidator(dateTimeProvider);


        LocalDateTime now = TODAY.atTime(12, 1);
        LocalDate tomorrow = TODAY.plusDays(1);
        LocalDate nextMonth = TODAY.plusMonths(1);

        when(dateTimeProvider.today()).thenReturn(TODAY);
        when(dateTimeProvider.now()).thenReturn(now);
        when(dateTimeProvider.tomorrow()).thenReturn(tomorrow);
        when(dateTimeProvider.nextMonth()).thenReturn(nextMonth);

    }


    @Test(expected = ValidationException.class)
    public void emailRequired() {
        LocalDate startDate = LocalDate.of(2020, 3, 9);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(null)
            .startDate(startDate)
            .endDate(startDate.plusDays(2));
        reservationValidator.validate(newReservationDto);
    }

    @Test(expected = ValidationException.class)
    public void fullnameRequired() {
        LocalDate startDate = LocalDate.of(2020, 3, 9);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(null)
            .email(EMAIL)
            .startDate(startDate)
            .endDate(startDate.plusDays(2));
        reservationValidator.validate(newReservationDto);
    }

    @Test(expected = ValidationException.class)
    public void startDateRequired() {
        LocalDate startDate = LocalDate.of(2020, 3, 9);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(null)
            .endDate(startDate.plusDays(2));
        reservationValidator.validate(newReservationDto);
    }

    @Test(expected = ValidationException.class)
    public void endDateRequired() {
        LocalDate startDate = LocalDate.of(2020, 3, 9);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(startDate)
            .endDate(null);
        reservationValidator.validate(newReservationDto);
    }

    @Test
    public void startDateLessThanOneDayInAdvance() {
        try {

            LocalDate tomorrow = TODAY.plusDays(1);

            NewReservationDto newReservationDto = new NewReservationDto()
                .fullname(FULLNAME)
                .email(EMAIL)
                .startDate(tomorrow)
                .endDate(tomorrow.plusDays(2));
            reservationValidator.validate(newReservationDto);
            fail("Should have thrown ValidationException but did not!");
        } catch (final ValidationException e) {
            final String message = "The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.";
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void startDateMoreThanOneDayInAdvance() {
        LocalDate tomorrow = TODAY.plusDays(1);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(tomorrow)
            .endDate(tomorrow.plusDays(1));

        LocalDateTime now = TODAY.atTime(11, 59);
        when(dateTimeProvider.now()).thenReturn(now);

        reservationValidator.validate(newReservationDto);
    }

    @Test(expected = ValidationException.class)
    public void startDateBeforeEndDate() {
        LocalDate startDate = LocalDate.of(2020, 3, 9);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(startDate)
            .endDate(startDate.minusDays(2));
        reservationValidator.validate(newReservationDto);
    }

    @Test(expected = ValidationException.class)
    public void reservationExceed3Days() {
        LocalDate startDate = LocalDate.of(2020, 3, 9);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(startDate)
            .endDate(startDate.plusDays(4));
        reservationValidator.validate(newReservationDto);
    }

    @Test(expected = ValidationException.class)
    public void reservationNotAfterNextMonth() {
        LocalDate nextMonth = TODAY.plusMonths(1);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(nextMonth)
            .endDate(nextMonth.plusDays(1));
        reservationValidator.validate(newReservationDto);
    }

    @Test
    public void reservationNextMonthMinusOneDayIsOK() {
        LocalDate nextMonth = TODAY.plusMonths(1);

        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(nextMonth.minusDays(1))
            .endDate(nextMonth);
        reservationValidator.validate(newReservationDto);
    }
}