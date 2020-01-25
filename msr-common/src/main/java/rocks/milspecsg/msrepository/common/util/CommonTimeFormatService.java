/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msrepository.common.util;

import rocks.milspecsg.msrepository.api.util.TimeFormatService;

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

    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss").withZone(ZoneOffset.UTC);

    private static Pattern timePattern = Pattern.compile(
        "\\s*(([1-9][0-9]*)\\s*[yY])?" +
            "\\s*(((1[0-2])|([1-9]))\\s*M)?" +
            "\\s*(((3[01])|([12][0-9])|[1-9])\\s*[dD])?" +
            "\\s*(((2[0-4])|(1[0-9])|[1-9])\\s*[hH])?" +
            "\\s*((([1-5][0-9])|[1-9])\\s*m)?" +
            "\\s*((([1-5][0-9])|[1-9])\\s*[sS])?\\s*"
    );

    @Override
    public long parseSecondsUnsafe(String input) {
        Matcher matcher = timePattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalStateException("Input does not match");
        }
        return Optional.ofNullable(matcher.group(2)).map(g -> Long.parseLong(g) * 31536000).orElse(0L)
            + Optional.ofNullable(matcher.group(4)).map(g -> Long.parseLong(g) * 2678400).orElse(0L)
            + Optional.ofNullable(matcher.group(8)).map(g -> Long.parseLong(g) * 864000).orElse(0L)
            + Optional.ofNullable(matcher.group(12)).map(g -> Long.parseLong(g) * 3600).orElse(0L)
            + Optional.ofNullable(matcher.group(16)).map(g -> Long.parseLong(g) * 60).orElse(0L)
            + Optional.ofNullable(matcher.group(19)).map(Long::parseLong).orElse(0L);
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
        long years = seconds / 31556952;
        seconds -= 31556952 * years;
        long months = seconds / 2592000;
        seconds -= 2628000 * months;
        long days = seconds / 86400;
        seconds -= 86400 * days;
        long hours = seconds / 3600;
        seconds -= 3600 * hours;
        long minutes = seconds / 60;
        seconds -= 60 * minutes;
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
            s.append(seconds).append(seconds == 1 ? " second, " : " seconds");
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
