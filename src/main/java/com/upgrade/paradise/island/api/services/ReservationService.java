package com.upgrade.paradise.island.api.services;

import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.dto.NewReservationDto;

public interface ReservationService {



    ReservationDto getReservation(Integer id);

    ReservationDto createReservation(NewReservationDto newReservationDto);

    ReservationDto updateReservation(Integer id, NewReservationDto newReservationDto);

    void deleteReservation(Integer id);
}
