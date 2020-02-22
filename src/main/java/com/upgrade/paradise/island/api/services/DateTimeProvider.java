package com.upgrade.paradise.island.api.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DateTimeProvider {


    LocalDate today();

    LocalDate tomorrow();

    LocalDate nextMonth();

    LocalDateTime now();
}
