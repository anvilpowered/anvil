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

package org.anvilpowered.anvil.common.registry;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.ZoneId;

@SuppressWarnings("UnstableApiUsage")
public class ZoneIdSerializer implements TypeSerializer<ZoneId> {

    private static final String AUTO = "auto";

    @Nullable
    @Override
    public ZoneId deserialize(TypeToken<?> type, ConfigurationNode node) {
        @Nullable final String input = node.getString();
        if (input == null || AUTO.equals(input)) {
            return ZoneId.systemDefault();
        } else {
            return ZoneId.of(input);
        }
    }

    @Override
    public void serialize(TypeToken<?> type, @Nullable ZoneId zoneId, ConfigurationNode node) {
        node.setValue(zoneId == null || zoneId.equals(ZoneId.systemDefault()) ? AUTO : zoneId.getId());
    }
}
