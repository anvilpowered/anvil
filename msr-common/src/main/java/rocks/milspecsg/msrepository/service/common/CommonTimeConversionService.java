/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.service.common;

import rocks.milspecsg.msrepository.api.TimeConversionService;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonTimeConversionService implements TimeConversionService {

    private static Pattern timePattern = Pattern.compile(
        "\\s*(([1-9][0-9]*)\\s*[yY])?" +
            "\\s*(((1[0-2])|([1-9]))\\s*M)?" +
            "\\s*(((3[01])|([12][0-9])|[1-9])\\s*[dD])?" +
            "\\s*(((2[0-4])|(1[0-9])|[1-9])\\s*[hH])?" +
            "\\s*((([1-5][0-9])|[1-9])\\s*m)?" +
            "\\s*((([1-5][0-9])|[1-9])\\s*[sS])?\\s*"
    );

    @Override
    public Integer parseUnsafe(String input) {
        Matcher matcher = timePattern.matcher(input);
        return Optional.ofNullable(matcher.group(2)).map(g -> Integer.parseInt(g) * 31536000).orElse(0)
            + Optional.ofNullable(matcher.group(4)).map(g -> Integer.parseInt(g) * 2678400).orElse(0)
            + Optional.ofNullable(matcher.group(8)).map(g -> Integer.parseInt(g) * 864000).orElse(0)
            + Optional.ofNullable(matcher.group(12)).map(g -> Integer.parseInt(g) * 3600).orElse(0)
            + Optional.ofNullable(matcher.group(16)).map(g -> Integer.parseInt(g) * 60).orElse(0)
            + Optional.ofNullable(matcher.group(19)).map(Integer::parseInt).orElse(0);
    }

    @Override
    public Optional<Integer> parse(String input) {
        try {
            return Optional.of(parseUnsafe(input));
        } catch (NumberFormatException | IndexOutOfBoundsException | IllegalStateException ignored) {
            return Optional.empty();
        }
    }
}
