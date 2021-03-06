package com.upgrade.paradise.island.api.controllers;


import com.upgrade.paradise.island.api.ReservationsApi;
import com.upgrade.paradise.island.api.controllers.validators.ReservationValidator;
import com.upgrade.paradise.island.api.dto.NewReservationDto;
import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.services.ReservationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@Api(tags = {"reservations"})
public class ReservationsController implements ReservationsApi {

    private ReservationService reservationService;
    private ReservationValidator reservationValidator;

    ReservationsController(@Autowired ReservationService reservationService,
                           @Autowired ReservationValidator reservationValidator) {
        this.reservationService = reservationService;
        this.reservationValidator = reservationValidator;
    }


    @Override
    public ResponseEntity<ReservationDto> getReservation(@PathVariable("reservationId") Integer reservationId) {

        return ResponseEntity.ok(reservationService.getReservation(reservationId));
    }

    @Override
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody NewReservationDto newReservationDto) {
        reservationValidator.validate(newReservationDto);
        ReservationDto reservation = reservationService.createReservation(newReservationDto);

        return ResponseEntity.ok(reservation);
    }

    @Override
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable("reservationId") Integer reservationId, @Valid @RequestBody NewReservationDto newReservationDto) {
        reservationValidator.validate(newReservationDto);
        ReservationDto reservation = reservationService.updateReservation(reservationId, newReservationDto);
        return ResponseEntity.ok(reservation);
    }

    @Override
    public ResponseEntity<Void> deleteReservation(@PathVariable("reservationId") Integer reservationId) {
        reservationService.deleteReservation(reservationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
