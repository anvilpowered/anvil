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

package org.anvilpowered.anvil.api.registry;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.ZoneId;

public class ZoneIdSerializer {

    private static final String AUTO = "auto";

    public static ZoneId parse(@Nullable String input) {
        if (input == null || AUTO.equals(input)) {
            return ZoneId.systemDefault();
        } else {
            return ZoneId.of(input);
        }
    }

    public static String toString(@Nullable ZoneId zoneId) {
        return zoneId == null || zoneId.equals(ZoneId.systemDefault()) ? AUTO : zoneId.getId();
    }
}
