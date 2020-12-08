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

package org.anvilpowered.anvil.api.registry;

import com.google.common.reflect.TypeToken;

import java.time.ZoneId;

@SuppressWarnings("UnstableApiUsage")
public final class TypeTokens {

    private TypeTokens() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static final TypeToken<Boolean> BOOLEAN = TypeToken.of(Boolean.class);
    public static final TypeToken<Integer> INTEGER = TypeToken.of(Integer.class);
    public static final TypeToken<String> STRING = TypeToken.of(String.class);
    public static final TypeToken<Void> VOID = TypeToken.of(Void.class);
    public static final TypeToken<ZoneId> ZONE_ID = TypeToken.of(ZoneId.class);
}
