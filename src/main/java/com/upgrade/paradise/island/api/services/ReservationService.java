package com.upgrade.paradise.island.api.services;

import com.upgrade.paradise.island.api.dto.ReservationDto;
import com.upgrade.paradise.island.api.dto.ReservationFieldsDto;

public interface ReservationService {



    ReservationDto getReservation(Integer id);

    ReservationDto createReservation(ReservationFieldsDto reservationFieldsDto);

    ReservationDto updateReservation(Integer id, ReservationFieldsDto reservationFieldsDto);

    void deleteReservation(Integer id);
}
