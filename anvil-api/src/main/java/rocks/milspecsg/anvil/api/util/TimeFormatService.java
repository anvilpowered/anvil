/*
 *   Anvil - MilSpecSG
 *   Copyright (C) 2020 Cableguy20
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

package rocks.milspecsg.anvil.api.util;

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

    String format(Duration duration);

    String format(Instant instant);

    String formatDurationUnsafe(String input);

    Optional<String> formatDuration(String input);

    String formatInstantUnsafe(String input);

    Optional<String> formatInstant(String input);
}
