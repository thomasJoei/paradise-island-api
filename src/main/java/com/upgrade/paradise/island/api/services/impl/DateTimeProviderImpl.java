package com.upgrade.paradise.island.api.services.impl;

import com.upgrade.paradise.island.api.services.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateTimeProviderImpl implements DateTimeProvider {

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }

    @Override
    public LocalDate nextMonth() {
        return LocalDate.now().plusMonths(1);
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
