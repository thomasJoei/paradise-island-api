package com.upgrade.paradise.island.api.services.impl;

import com.upgrade.paradise.island.api.db.dao.ReservationRepository;
import com.upgrade.paradise.island.api.db.model.Reservation;
import com.upgrade.paradise.island.api.db.model.ReservedDay;
import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.exceptions.CampsiteNotAvailableException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    private ReservationServiceImpl reservationService;

    private static final String FULLNAME = "fullname";
    private static final String EMAIL = "email";
    private static final LocalDate START_DATE = LocalDate.of(2020, 3, 5);
    private static final LocalDate END_DATE = LocalDate.of(2020, 3, 6);


    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        reservationService = new ReservationServiceImpl(reservationRepository);
    }

    @Test
    public void getReservation() {
        List<ReservedDay> reservedDays = new ArrayList<>();
        reservedDays.add(new ReservedDay().date(START_DATE));

        Reservation persisted = new Reservation()
            .id(1)
            .fullname(FULLNAME)
            .email(EMAIL);
        persisted.setReservedDays(reservedDays);

        when(reservationRepository.findById(eq(1))).thenReturn(Optional.of(persisted));

        ReservationDto result = reservationService.getReservation(1);

        assertThat(result.getFullname()).isEqualTo(FULLNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getStartDate()).isEqualTo(START_DATE);
        assertThat(result.getEndDate()).isEqualTo(END_DATE);
    }

    @Test
    public void createReservation() {
        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(START_DATE)
            .endDate(END_DATE);

        List<ReservedDay> reservedDays = new ArrayList<>();
        reservedDays.add(new ReservedDay().date(START_DATE));

        Reservation persisted = new Reservation()
            .id(1)
            .fullname(FULLNAME)
            .email(EMAIL);
        persisted.setReservedDays(reservedDays);

        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(persisted);


        reservationService.createReservation(newReservationDto);


        ArgumentCaptor<Reservation> argumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository, times(1)).saveAndFlush(argumentCaptor.capture());

        List<Reservation> capturedArgument = argumentCaptor.getAllValues();

        assertThat(capturedArgument.get(0).getFullname()).isEqualTo(FULLNAME);
        assertThat(capturedArgument.get(0).getEmail()).isEqualTo(EMAIL);
        assertThat(capturedArgument.get(0).getReservedDays()).hasSize(1);
        assertThat(capturedArgument.get(0).getReservedDays().get(0).getDate()).isEqualTo(START_DATE);
    }

    @Test(expected = CampsiteNotAvailableException.class)
    public void createReservationCampAlreadyReserved() {
        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(START_DATE)
            .endDate(END_DATE);

        when(reservationRepository.saveAndFlush(any(Reservation.class)))
            .thenThrow(new DataIntegrityViolationException("fail"));

        reservationService.createReservation(newReservationDto);
    }

    @Test
    public void updateReservation() {
        NewReservationDto newReservationDto = new NewReservationDto()
            .fullname(FULLNAME)
            .email(EMAIL)
            .startDate(START_DATE.plusDays(1))
            .endDate(END_DATE.plusDays(1));

        List<ReservedDay> reservedDays = new ArrayList<>();
        reservedDays.add(new ReservedDay().date(START_DATE));
        Reservation persisted = new Reservation()
            .id(1)
            .fullname(FULLNAME)
            .email(EMAIL);
        persisted.setReservedDays(reservedDays);

        List<ReservedDay> newReservedDays = new ArrayList<>();
        newReservedDays.add(new ReservedDay().date(START_DATE.plusDays(1)));
        Reservation newPersisted = new Reservation()
            .id(1)
            .fullname(FULLNAME)
            .email(EMAIL);
        newPersisted.setReservedDays(newReservedDays);

        when(reservationRepository.existsById(eq(1))).thenReturn(true);
        when(reservationRepository.getOne(eq(1))).thenReturn(persisted);
        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(newPersisted);


        reservationService.updateReservation(1, newReservationDto);


        ArgumentCaptor<Reservation> argumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveAndFlush(argumentCaptor.capture());

        List<Reservation> capturedArgument = argumentCaptor.getAllValues();

        assertThat(capturedArgument.get(0).getFullname()).isEqualTo(FULLNAME);
        assertThat(capturedArgument.get(0).getEmail()).isEqualTo(EMAIL);
        assertThat(capturedArgument.get(0).getReservedDays()).hasSize(1);
        assertThat(capturedArgument.get(0).getReservedDays().get(0).getDate()).isEqualTo(START_DATE.plusDays(1));
    }

    @Test
    public void deleteReservation() {
        when(reservationRepository.existsById(eq(1))).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(eq(1));

        reservationService.deleteReservation(1);

        verify(reservationRepository).deleteById(eq(1));
    }
}