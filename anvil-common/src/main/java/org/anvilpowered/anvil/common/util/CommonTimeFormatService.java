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

    private static final DateTimeFormatter[] dateTimeFormat = {
        DateTimeFormatter.ofPattern("ss").withZone(ZoneOffset.UTC),
        DateTimeFormatter.ofPattern("mm:ss").withZone(ZoneOffset.UTC),
        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneOffset.UTC),
        DateTimeFormatter.ofPattern("dd-HH:mm:ss").withZone(ZoneOffset.UTC),
        DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss").withZone(ZoneOffset.UTC),
        DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss").withZone(ZoneOffset.UTC)
    };

    private static final Pattern timePattern = Pattern.compile(
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
        return Instant.from(dateTimeFormat[5].parse(input));
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
    public FormatResult format(Duration duration) {
        return new DurationFormatResult(duration);
    }

    @Override
    public FormatResult format(Instant instant) {
        return new InstantFormatResult(instant);
    }

    @Override
    public FormatResult formatDurationUnsafe(String input) {
        return format(parseDurationUnsafe(input));
    }

    @Override
    public Optional<FormatResult> formatDuration(String input) {
        return parseDuration(input).map(this::format);
    }

    @Override
    public FormatResult formatInstantUnsafe(String input) {
        return format(parseInstantUnsafe(input));
    }

    @Override
    public Optional<FormatResult> formatInstant(String input) {
        return parseInstant(input).map(this::format);
    }

    private static final class DurationFormatResult implements FormatResult {

        private final Duration duration;
        int maxCharacters;
        int maxUnits;
        boolean withoutNano;

        private DurationFormatResult(Duration duration) {
            this.duration = duration;
            maxCharacters = -1;
            maxUnits = -1;
            withoutNano = false;
        }

        @Override
        public FormatResult maxCharacters(int maxCharacters) {
            this.maxCharacters = maxCharacters;
            return this;
        }

        @Override
        public FormatResult maxUnits(int maxUnits) {
            this.maxUnits = maxUnits;
            return this;
        }

        @Override
        public FormatResult withoutNano() {
            withoutNano = true;
            return this;
        }

        @Override
        public String get() {
            if (maxCharacters == 0 || maxUnits == 0) {
                return "";
            }
            StringBuilder s = new StringBuilder();
            long seconds = duration.getSeconds();
            int nanos = duration.getNano();
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
            int units = maxUnits < 0 ? Integer.MIN_VALUE : 0;
            if (years != 0) {
                String t = years + (years == 1 ? " year, " : " years, ");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    ++units;
                }
            }
            if (months != 0 && units <= maxUnits) {
                String t = months + (months == 1 ? " month, " : " months, ");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    ++units;
                }
            }
            if (days != 0 && units <= maxUnits) {
                String t = days + (days == 1 ? " day, " : " days, ");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    ++units;
                }
            }
            if (hours != 0 && units <= maxUnits) {
                String t = hours + (hours == 1 ? " hour, " : " hours, ");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    ++units;
                }
            }
            if (minutes != 0 && units <= maxUnits) {
                String t = minutes + (minutes == 1 ? " minute, " : " minutes, ");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    ++units;
                }
            }
            if (seconds != 0 && units <= maxUnits) {
                String t = seconds + (seconds == 1 ? " second, " : " seconds, ");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    ++units;
                }
            }
            if (nanos != 0 && !withoutNano && units <= maxUnits) {
                String t = nanos + (nanos == 1 ? " nanosecond" : " nanoseconds");
                if (s.length() + t.length() <= maxCharacters) {
                    s.append(t);
                    return s.toString();
                }
            }
            if (s.length() > 1) {
                s.deleteCharAt(s.length() - 1);
                s.deleteCharAt(s.length() - 1);
            }
            return s.toString();
        }
    }

    private static final class InstantFormatResult implements FormatResult {

        private final Instant instant;
        int maxUnits;
        boolean withoutNano;

        private InstantFormatResult(Instant instant) {
            this.instant = instant;
            maxUnits = -1;
            withoutNano = false;
        }

        @Override
        public FormatResult maxCharacters(int maxCharacters) {
            if (maxCharacters < 0 || maxCharacters > 18) {
                maxUnits = -1;
            } else if (maxCharacters > 13) {
                maxUnits = 5;
            } else if (maxCharacters > 10) {
                maxUnits = 4;
            } else if (maxCharacters > 7) {
                maxUnits = 3;
            } else if (maxCharacters > 4) {
                maxUnits = 2;
            } else if (maxCharacters > 1) {
                maxUnits = 1;
            } else {
                maxUnits = 0;
            }
            return this;
        }

        @Override
        public FormatResult maxUnits(int maxUnits) {
            this.maxUnits = maxUnits;
            return this;
        }

        @Override
        public FormatResult withoutNano() {
            return this;
        }

        @Override
        public String get() {
            if (maxUnits == 0) {
                return "";
            }
            if (maxUnits < 0 || maxUnits > 6) {
                return dateTimeFormat[5].format(instant);
            }
            return dateTimeFormat[maxUnits - 1].format(instant);
        }
    }
}
