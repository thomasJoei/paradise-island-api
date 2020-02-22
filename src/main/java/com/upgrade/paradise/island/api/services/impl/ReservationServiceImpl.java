package com.upgrade.paradise.island.api.services.impl;

import com.upgrade.paradise.island.api.db.dao.ReservationRepository;
import com.upgrade.paradise.island.api.db.model.Reservation;
import com.upgrade.paradise.island.api.db.model.ReservedDay;
import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.dto.ReservationFieldsDto;
import com.upgrade.paradise.island.api.exceptions.CampsiteNotAvailableException;
import com.upgrade.paradise.island.api.exceptions.NotFoundException;
import com.upgrade.paradise.island.api.services.ReservationService;
import com.upgrade.paradise.island.api.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private ReservationRepository reservationRepository;

    ReservationServiceImpl(@Autowired ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    @Override
    public ReservationDto createReservation(ReservationFieldsDto reservationFieldsDto) {
        Reservation reservation = reservationFromDto(reservationFieldsDto);

        try {
            reservation = reservationRepository.saveAndFlush(reservation);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            // need to update for conflict or bad request
            throw new CampsiteNotAvailableException();
        }

        return reservationDtoFromModel(reservation);
    }

    @Override
    @Transactional
    public ReservationDto updateReservation(Integer id, ReservationFieldsDto reservationFieldsDto) {
        validateId(id);
        Reservation newReservation = reservationFromDto(reservationFieldsDto);
        newReservation.setId(id);

        try {
            syncReservedDays(newReservation);
            newReservation = reservationRepository.saveAndFlush(newReservation);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new CampsiteNotAvailableException();
        }
        return reservationDtoFromModel(newReservation);

    }

    @Override
    public void deleteReservation(Integer id) {
        validateId(id);
        reservationRepository.deleteById(id);
    }

    private void syncReservedDays(Reservation newReservation) {
        Reservation oldReservation = reservationRepository.getOne(newReservation.getId());

        Map<LocalDate, ReservedDay> oldReservedDayMap = oldReservation.getReservedDays()
            .stream()
            .collect(Collectors.toMap(ReservedDay::getDate, d -> d));

        // if the date was already in the old reservation we keep it
        List<ReservedDay> newReservedDays = newReservation.getReservedDays()
            .stream()
            .map(rd -> oldReservedDayMap.getOrDefault(rd.getDate(), rd))
            .collect(Collectors.toList());

        newReservation.setReservedDays(newReservedDays);
    }


    private void validateId(Integer id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("Reservation does not exist.");
        }
    }

    private ReservationDto reservationDtoFromModel(Reservation reservation) {
        List<ReservedDay> reservedDays = reservation.getReservedDays();

        return (ReservationDto) new ReservationDto()
            .id(reservation.getId())
            .fullname(reservation.getFullname())
            .email(reservation.getEmail())
            .startDate(getStartDate(reservedDays))
            .endDate(getEndDate(reservedDays));
    }

    private LocalDate getStartDate(List<ReservedDay> reservedDays) {
        return reservedDays.stream()
            .min(Comparator.comparing(ReservedDay::getDate))
            .orElseThrow(() -> new RuntimeException("Reservation has no reserved days"))
            .getDate();
    }

    private LocalDate getEndDate(List<ReservedDay> reservedDays) {
        return reservedDays.stream()
            .max(Comparator.comparing(ReservedDay::getDate))
            .orElseThrow(() -> new RuntimeException("Reservation has no reserved days"))
            .getDate()
            .plusDays(1);
    }

    private Reservation reservationFromDto(ReservationFieldsDto reservationFieldsDto) {
        Reservation reservation = new Reservation()
            .fullname(reservationFieldsDto.getFullname())
            .email(reservationFieldsDto.getEmail());
        reservation.setReservedDays(createReservedDays(reservationFieldsDto, reservation));
        return reservation;
    }


    private List<ReservedDay> createReservedDays(ReservationFieldsDto reservationFieldsDto, Reservation reservation) {
        return DateUtils.getDatesStream(reservationFieldsDto.getStartDate(), reservationFieldsDto.getEndDate())
            .map(date -> new ReservedDay()
                .date(date)
                .reservation(reservation)
            )
            .collect(Collectors.toList());
    }

}
