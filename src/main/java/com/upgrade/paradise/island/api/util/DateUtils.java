package com.upgrade.paradise.island.api.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class DateUtils {

    public static Stream<LocalDate> getDatesStream(LocalDate startDate, LocalDate endDate) {
        final int days = (int) startDate.until(endDate, ChronoUnit.DAYS);
        return Stream.iterate(startDate, d -> d.plusDays(1))
            .limit(days);
    }
}
