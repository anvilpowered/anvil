/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
package org.anvilpowered.anvil.common.util

import org.anvilpowered.anvil.api.Anvil.Companion.registry
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.anvil.api.util.TimeFormatService.FormatResult
import java.time.DateTimeException
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Optional
import java.util.regex.Pattern

class CommonTimeFormatService : TimeFormatService {
    override fun parseSecondsUnsafe(input: String?): Long {
        val matcher = timePattern.matcher(input)
        check(matcher.matches()) { "Input does not match" }
        return (Optional.ofNullable(matcher.group("years"))
            .map { g: String -> g.toLong() * SECONDS_IN_YEAR }.orElse(0L)
            + Optional.ofNullable(matcher.group("months"))
            .map { g: String -> g.toLong() * SECONDS_IN_MONTH }.orElse(0L)
            + Optional.ofNullable(matcher.group("weeks"))
            .map { g: String -> g.toLong() * SECONDS_IN_WEEK }.orElse(0L)
            + Optional.ofNullable(matcher.group("days"))
            .map { g: String -> g.toLong() * SECONDS_IN_DAY }.orElse(0L)
            + Optional.ofNullable(matcher.group("hours"))
            .map { g: String -> g.toLong() * SECONDS_IN_HOUR }.orElse(0L)
            + Optional.ofNullable(matcher.group("minutes"))
            .map { g: String -> g.toLong() * SECONDS_IN_MINUTE }.orElse(0L)
            + Optional.ofNullable(matcher.group("seconds"))
            .map { s: String -> s.toLong() }.orElse(0L))
    }

    override fun parseSeconds(input: String?): Long? {
        return try {
            parseSecondsUnsafe(input)
        } catch (ignored: NumberFormatException) {
            null
        } catch (ignored: IndexOutOfBoundsException) {
            null
        } catch (ignored: IllegalStateException) {
            null
        }
    }

    override fun parseDurationUnsafe(input: String?): Duration? {
        return Duration.ofSeconds(parseSecondsUnsafe(input))
    }

    override fun parseDuration(input: String?): Duration? {
        return Duration.ofSeconds(parseSeconds(input) ?: 0)
    }

    override fun parseFutureInstantUnsafe(input: String?): Instant? {
        return utcNow().plusSeconds(parseSecondsUnsafe(input))
    }

    override fun parseFutureInstant(input: String?): Instant? {
        return utcNow().plusSeconds(parseSecondsUnsafe(input))
    }

    override fun parseInstantUnsafe(input: String?): Instant? {
        return Instant.from(getZonedFormatter(5).parse(input))
    }

    override fun parseInstant(input: String?): Instant? {
        return try {
            parseInstantUnsafe(input)
        } catch (ignored: DateTimeException) {
            null
        } catch (ignored: NullPointerException) {
            null
        }
    }

    override fun fromUTC(instant: Instant?): ZonedDateTime? {
        return instant!!.atZone(zone)
    }

    override fun format(duration: Duration?): FormatResult? {
        return DurationFormatResult(duration)
    }

    override fun format(temporal: TemporalAccessor?): FormatResult? {
        return TemporalFormatResult(temporal)
    }

    override fun formatDurationUnsafe(input: String?): FormatResult? {
        return format(parseDurationUnsafe(input))
    }

    override fun formatDuration(input: String?): FormatResult? {
        return this.format(parseDuration(input) ?: Duration.ZERO)
    }

    override fun formatInstantUnsafe(input: String?): FormatResult? {
        return format(parseInstantUnsafe(input))
    }

    override fun formatInstant(input: String?): FormatResult? {
        return this.format(parseInstant(input) ?: Instant.now())
    }

    private class DurationFormatResult(private val duration: Duration?) : FormatResult {
        var maxCharacters: Int
        var maxUnits: Int
        var withoutNano: Boolean
        override fun maxCharacters(maxCharacters: Int): FormatResult? {
            this.maxCharacters = maxCharacters
            return this
        }

        override fun maxUnits(maxUnits: Int): FormatResult? {
            this.maxUnits = maxUnits
            return this
        }

        override fun withoutNano(): FormatResult? {
            withoutNano = true
            return this
        }

        override fun toString(): String {
            if (maxCharacters == 0 || maxUnits == 0) {
                return ""
            }
            val s = StringBuilder()
            var seconds = duration!!.seconds
            val nanos = duration.nano
            if (seconds == 0L) {
                return if (nanos > 0) "$nanos nanoseconds" else "0 seconds"
            }
            val years = seconds / SECONDS_IN_YEAR
            seconds -= SECONDS_IN_YEAR * years
            val months = seconds / SECONDS_IN_MONTH
            seconds -= SECONDS_IN_MONTH * months
            val weeks = seconds / SECONDS_IN_WEEK
            seconds -= SECONDS_IN_WEEK * weeks
            val days = seconds / SECONDS_IN_DAY
            seconds -= SECONDS_IN_DAY * days
            val hours = seconds / SECONDS_IN_HOUR
            seconds -= SECONDS_IN_HOUR * hours
            val minutes = seconds / SECONDS_IN_MINUTE
            seconds -= SECONDS_IN_MINUTE * minutes
            var units = if (maxUnits < 0) Int.MIN_VALUE else 0
            val maxCharacters = if (maxCharacters < 0) Int.MAX_VALUE else maxCharacters
            if (years != 0L) {
                val t = years.toString() + if (years == 1L) " year, " else " years, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (months != 0L && units <= maxUnits) {
                val t = months.toString() + if (months == 1L) " month, " else " months, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (weeks != 0L && units <= maxUnits) {
                val t = weeks.toString() + if (weeks == 1L) " week, " else " weeks, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (days != 0L && units <= maxUnits) {
                val t = days.toString() + if (days == 1L) " day, " else " days, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (hours != 0L && units <= maxUnits) {
                val t = hours.toString() + if (hours == 1L) " hour, " else " hours, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (minutes != 0L && units <= maxUnits) {
                val t = minutes.toString() + if (minutes == 1L) " minute, " else " minutes, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (seconds != 0L && units <= maxUnits) {
                val t = seconds.toString() + if (seconds == 1L) " second, " else " seconds, "
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    ++units
                }
            }
            if (nanos != 0 && !withoutNano && units <= maxUnits) {
                val t = nanos.toString() + if (nanos == 1) " nanosecond" else " nanoseconds"
                if (s.length + t.length <= maxCharacters) {
                    s.append(t)
                    return s.toString()
                }
            }
            if (s.length > 1) {
                s.deleteCharAt(s.length - 1)
                s.deleteCharAt(s.length - 1)
            }
            return s.toString()
        }

        init {
            maxCharacters = -1
            maxUnits = -1
            withoutNano = false
        }
    }

    private class TemporalFormatResult(private val temporal: TemporalAccessor?) : FormatResult {
        var maxUnits: Int
        var withoutNano: Boolean
        override fun maxCharacters(maxCharacters: Int): FormatResult? {
            maxUnits = if (maxCharacters < 0 || maxCharacters > 18) {
                -1
            } else if (maxCharacters > 13) {
                5
            } else if (maxCharacters > 10) {
                4
            } else if (maxCharacters > 7) {
                3
            } else if (maxCharacters > 4) {
                2
            } else if (maxCharacters > 1) {
                1
            } else {
                0
            }
            return this
        }

        override fun maxUnits(maxUnits: Int): FormatResult? {
            this.maxUnits = maxUnits
            return this
        }

        override fun withoutNano(): FormatResult? {
            return this
        }

        override fun toString(): String {
            if (maxUnits == 0) {
                return ""
            }
            return if (maxUnits < 0 || maxUnits > 6) {
                getZonedFormatter(5).format(temporal)
            } else getZonedFormatter(maxUnits - 1).format(temporal)
        }

        init {
            maxUnits = -1
            withoutNano = false
        }
    }

    companion object {
        private const val SECONDS_IN_YEAR: Long = 31536000
        private const val SECONDS_IN_MONTH: Long = 2628000
        private const val SECONDS_IN_WEEK: Long = 604800
        private const val SECONDS_IN_DAY: Long = 86400
        private const val SECONDS_IN_HOUR: Long = 3600
        private const val SECONDS_IN_MINUTE: Long = 60
        private val dateTimeFormat = arrayOf(
            DateTimeFormatter.ofPattern("ss").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ofPattern("mm:ss").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ofPattern("dd-HH:mm:ss").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss").withZone(ZoneOffset.UTC),
            DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss").withZone(ZoneOffset.UTC)
        )
        private val dateTimeFormatZoned = dateTimeFormat.clone()
        private val timePattern = Pattern.compile(
            "\\s*((?<years>-?[0-9]*)\\s*[yY])?" +
                "\\s*((?<months>-?[0-9]*)\\s*M)?" +
                "\\s*((?<weeks>-?[0-9]*)\\s*[wW])?" +
                "\\s*((?<days>-?[0-9]*)\\s*[dD])?" +
                "\\s*((?<hours>-?[0-9]*)\\s*[hH])?" +
                "\\s*((?<minutes>-?[0-9]*)\\s*m)?" +
                "\\s*((?<seconds>-?[0-9]*)\\s*[sS])?\\s*"
        )

        private fun getZonedFormatter(index: Int): DateTimeFormatter {
            val result = dateTimeFormatZoned[index]
            val zone = zone
            return if (result.zone == zone) {
                result
            } else dateTimeFormat[index].withZone(zone).also { dateTimeFormatZoned[index] = it }
        }

        private val zone: ZoneId
            private get() = registry.getOrDefault(Keys.TIME_ZONE)

        private fun utcNow(): Instant {
            return OffsetDateTime.now(ZoneOffset.UTC).toInstant()
        }
    }
}