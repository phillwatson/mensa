package com.hillayes.mensa.auditor.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class DateUtils {
    public static Optional<LocalDateTime> toLocalDateTime(Instant aInstant) {
        if (aInstant == null) {
            return Optional.empty();
        }

        return Optional.of(LocalDateTime.ofInstant(aInstant, ZoneOffset.UTC));
    }
}
