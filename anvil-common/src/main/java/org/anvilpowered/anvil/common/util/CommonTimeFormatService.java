/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.common.util;

import org.anvilpowered.anvil.api.util.TimeFormatService;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonTimeFormatService implements TimeFormatService {

    private static final long SECONDS_IN_YEAR = 31536000;
    private static final long SECONDS_IN_MONTH = 2628000;
    private static final long SECONDS_IN_DAY = 86400;
    private static final long SECONDS_IN_HOUR = 3600;
    private static final long SECONDS_IN_MINUTE = 60;

    private static DateTimeFormatter dateTimeFormat
        = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss").withZone(ZoneOffset.UTC);

    private static Pattern timePattern = Pattern.compile(
        "\\s*((?<years>-?[0-9]*)\\s*[yY])?" +
            "\\s*((?<months>-?[0-9]*)\\s*M)?" +
            "\\s*((?<days>-?[0-9]*)\\s*[dD])?" +
            "\\s*((?<hours>-?[0-9]*)\\s*[hH])?" +
            "\\s*((?<minutes>-?[0-9]*)\\s*m)?" +
            "\\s*((?<seconds>-?[0-9]*)\\s*[sS])?\\s*"
    );

    @Override
    public long parseSecondsUnsafe(String input) {
        Matcher matcher = timePattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalStateException("Input does not match");
        }
        return Optional.ofNullable(matcher.group("years"))
            .map(g -> Long.parseLong(g) * SECONDS_IN_YEAR).orElse(0L)
            + Optional.ofNullable(matcher.group("months"))
            .map(g -> Long.parseLong(g) * SECONDS_IN_MONTH).orElse(0L)
            + Optional.ofNullable(matcher.group("days"))
            .map(g -> Long.parseLong(g) * SECONDS_IN_DAY).orElse(0L)
            + Optional.ofNullable(matcher.group("hours"))
            .map(g -> Long.parseLong(g) * SECONDS_IN_HOUR).orElse(0L)
            + Optional.ofNullable(matcher.group("minutes"))
            .map(g -> Long.parseLong(g) * SECONDS_IN_MINUTE).orElse(0L)
            + Optional.ofNullable(matcher.group("seconds"))
            .map(Long::parseLong).orElse(0L);
    }

    @Override
    public Optional<Long> parseSeconds(String input) {
        try {
            return Optional.of(parseSecondsUnsafe(input));
        } catch (NumberFormatException | IndexOutOfBoundsException | IllegalStateException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Duration parseDurationUnsafe(String input) {
        return Duration.ofSeconds(parseSecondsUnsafe(input));
    }

    @Override
    public Optional<Duration> parseDuration(String input) {
        return parseSeconds(input).map(Duration::ofSeconds);
    }

    @Override
    public Instant parseFutureInstantUnsafe(String input) {
        return OffsetDateTime.now(ZoneOffset.UTC).toInstant().plusSeconds(parseSecondsUnsafe(input));
    }

    @Override
    public Optional<Instant> parseFutureInstant(String input) {
        return parseSeconds(input).map(s -> OffsetDateTime.now(ZoneOffset.UTC).toInstant().plusSeconds(s));
    }

    @Override
    public Instant parseInstantUnsafe(String input) {
        return Instant.from(dateTimeFormat.parse(input));
    }

    @Override
    public Optional<Instant> parseInstant(String input) {
        try {
            return Optional.of(parseInstantUnsafe(input));
        } catch (DateTimeException | NullPointerException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public String format(Duration duration) {
        StringBuilder s = new StringBuilder();
        long seconds = duration.getSeconds();
        long nanos = duration.getNano();
        if (seconds == 0) {
            return nanos > 0 ? nanos + " nanoseconds" : "0 seconds";
        }
        long years = seconds / SECONDS_IN_YEAR;
        seconds -= SECONDS_IN_YEAR * years;
        long months = seconds / SECONDS_IN_MONTH;
        seconds -= SECONDS_IN_MONTH * months;
        long days = seconds / SECONDS_IN_DAY;
        seconds -= SECONDS_IN_DAY * days;
        long hours = seconds / SECONDS_IN_HOUR;
        seconds -= SECONDS_IN_HOUR * hours;
        long minutes = seconds / SECONDS_IN_MINUTE;
        seconds -= SECONDS_IN_MINUTE * minutes;
        if (years != 0) {
            s.append(years).append(years == 1 ? " year, " : " years, ");
        }
        if (months != 0) {
            s.append(months).append(months == 1 ? " month, " : " months, ");
        }
        if (days != 0) {
            s.append(days).append(days == 1 ? " day, " : " days, ");
        }
        if (hours != 0) {
            s.append(hours).append(hours == 1 ? " hour, " : " hours, ");
        }
        if (minutes != 0) {
            s.append(minutes).append(minutes == 1 ? " minute, " : " minutes, ");
        }
        if (seconds != 0) {
            s.append(seconds).append(seconds == 1 ? " second, " : " seconds, ");
        }
        if (nanos != 0) {
            s.append(nanos).append(nanos == 1 ? " nanosecond" : " nanoseconds");
        } else if (s.length() > 1) {
            s.deleteCharAt(s.length() - 1);
            s.deleteCharAt(s.length() - 1);
        }
        return s.toString();
    }

    @Override
    public String format(Instant instant) {
        return dateTimeFormat.format(instant);
    }

    @Override
    public String formatDurationUnsafe(String input) {
        return format(parseDurationUnsafe(input));
    }

    @Override
    public Optional<String> formatDuration(String input) {
        return parseDuration(input).map(this::format);
    }

    @Override
    public String formatInstantUnsafe(String input) {
        return format(parseInstantUnsafe(input));
    }

    @Override
    public Optional<String> formatInstant(String input) {
        return parseInstant(input).map(this::format);
    }
}
