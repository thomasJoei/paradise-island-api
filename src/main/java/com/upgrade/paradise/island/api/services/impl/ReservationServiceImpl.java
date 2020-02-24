package com.upgrade.paradise.island.api.services.impl;

import com.upgrade.paradise.island.api.db.dao.ReservationRepository;
import com.upgrade.paradise.island.api.db.model.Reservation;
import com.upgrade.paradise.island.api.db.model.ReservedDay;
import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.dto.ReservationDto;
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
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private ReservationRepository reservationRepository;

    ReservationServiceImpl(@Autowired ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }


    @Override
    public ReservationDto getReservation(Integer id) {
        return reservationDtoFromModel(
            reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation does not exist.")
                )
        );
    }


    @Override
    @Transactional
    public ReservationDto createReservation(NewReservationDto newReservationDto) {
        Reservation reservation = reservationFromDto(newReservationDto);

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
    public ReservationDto updateReservation(Integer id, NewReservationDto newReservationDto) {
        try {
            validateId(id);
            Reservation newReservation = reservationFromDto(newReservationDto);

            Reservation reservation = reservationRepository.getOne(id);
            reservation.fullname(newReservation.getFullname())
                .email(newReservation.getEmail())
                .reservedDays(newReservation.getReservedDays());

            reservation = reservationRepository.saveAndFlush(reservation);
            return reservationDtoFromModel(reservation);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new CampsiteNotAvailableException();
        }
    }

    @Override
    public void deleteReservation(Integer id) {
        validateId(id);
        reservationRepository.deleteById(id);
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

    private Reservation reservationFromDto(NewReservationDto newReservationDto) {
        Reservation reservation = new Reservation()
            .fullname(newReservationDto.getFullname())
            .email(newReservationDto.getEmail());
        reservation.setReservedDays(createReservedDays(newReservationDto, reservation));
        return reservation;
    }


    private List<ReservedDay> createReservedDays(NewReservationDto newReservationDto, Reservation reservation) {
        return DateUtils.getDatesStream(newReservationDto.getStartDate(), newReservationDto.getEndDate())
            .map(date -> new ReservedDay()
                .date(date)
                .reservation(reservation)
            )
            .collect(Collectors.toList());
    }

}
