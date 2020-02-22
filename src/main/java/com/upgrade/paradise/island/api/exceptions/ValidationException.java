package com.upgrade.paradise.island.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
