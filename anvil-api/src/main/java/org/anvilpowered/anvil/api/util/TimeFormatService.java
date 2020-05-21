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

package org.anvilpowered.anvil.api.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public interface TimeFormatService {

    long parseSecondsUnsafe(String input);

    Optional<Long> parseSeconds(String input);

    Duration parseDurationUnsafe(String input);

    Optional<Duration> parseDuration(String input);

    Instant parseFutureInstantUnsafe(String input);

    Optional<Instant> parseFutureInstant(String input);

    Instant parseInstantUnsafe(String input);

    Optional<Instant> parseInstant(String input);

    FormatResult format(Duration duration);

    FormatResult format(Instant instant);

    FormatResult formatDurationUnsafe(String input);

    Optional<FormatResult> formatDuration(String input);

    FormatResult formatInstantUnsafe(String input);

    Optional<FormatResult> formatInstant(String input);

    interface FormatResult {

        /**
         * Sets the maximum amount of characters in the result. Providing a
         * negative value means no maximum.
         *
         * @param maxCharacters The maximum amount of characters
         * @return {@code this}
         */
        FormatResult maxCharacters(int maxCharacters);

        /**
         * Sets the maximum amount of units in the result. Providing a
         * negative value means no maximum.
         *
         * @param maxUnits The maximum amount of units
         * @return {@code this}
         */
        FormatResult maxUnits(int maxUnits);

        /**
         * Removes the nano second component of the result
         *
         * @return {@code this}
         */
        FormatResult withoutNano();

        /**
         * Builds and returns the string representation of this {@link FormatResult}
         *
         * @return The string representation of this {@link FormatResult}
         */
        @Override
        String toString();
    }
}
