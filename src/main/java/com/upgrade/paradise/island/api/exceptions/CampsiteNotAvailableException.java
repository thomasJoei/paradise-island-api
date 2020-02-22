package com.upgrade.paradise.island.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class CampsiteNotAvailableException extends ValidationException {

    public CampsiteNotAvailableException() {
        super("Campsite not available for the selected date range.");
    }
}
