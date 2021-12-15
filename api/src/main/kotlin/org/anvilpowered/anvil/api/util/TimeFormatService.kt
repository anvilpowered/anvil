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
package org.anvilpowered.anvil.api.util

import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor
import java.time.Duration
import java.util.Optional

/**
 * A service that deals with transforming times and durations to and from human-readable formats.
 *
 *
 *
 * In general, times are interpreted in the following way:
 *
 *
 *  * [String] and [ZonedDateTime] - In the end user's time zone (as defined in the config)
 *  * [Instant] - In UTC
 *
 */
interface TimeFormatService {
  fun parseSecondsUnsafe(input: String?): Long
  fun parseSeconds(input: String?): Long?
  fun parseDurationUnsafe(input: String?): Duration?
  fun parseDuration(input: String?): Duration?
  fun parseFutureInstantUnsafe(input: String?): Instant?
  fun parseFutureInstant(input: String?): Instant?
  fun parseInstantUnsafe(input: String?): Instant?
  fun parseInstant(input: String?): Instant?

  /**
   * Interprets the provided [Instant] as UTC and converts it into the time zone defined in the config.
   *
   * @param instant The [Instant] in UTC to convert
   * @return An [Instant] converted to the time zone defined in the config
   */
  fun fromUTC(instant: Instant?): ZonedDateTime?
  fun format(duration: Duration?): FormatResult?

  /**
   * Formats the provided [TemporalAccessor] and converts it to the time zone defined in the config if it does
   * not already have timezone data.
   *
   * @param temporal The [TemporalAccessor] to format
   * @return A [FormatResult]
   */
  fun format(temporal: TemporalAccessor?): FormatResult?
  fun formatDurationUnsafe(input: String?): FormatResult?
  fun formatDuration(input: String?): FormatResult?
  fun formatInstantUnsafe(input: String?): FormatResult?
  fun formatInstant(input: String?): FormatResult?
  interface FormatResult {
    /**
     * Sets the maximum amount of characters in the result. Providing a
     * negative value means no maximum.
     *
     * @param maxCharacters The maximum amount of characters
     * @return `this`
     */
    fun maxCharacters(maxCharacters: Int): FormatResult?

    /**
     * Sets the maximum amount of units in the result. Providing a
     * negative value means no maximum.
     *
     * @param maxUnits The maximum amount of units
     * @return `this`
     */
    fun maxUnits(maxUnits: Int): FormatResult?

    /**
     * Removes the nano-second component of the result
     *
     * @return `this`
     */
    fun withoutNano(): FormatResult?

    /**
     * Builds and returns the string representation of this [FormatResult]
     *
     * @return The string representation of this [FormatResult]
     */
    override fun toString(): String
  }
}
