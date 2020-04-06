/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api.core.data.key;

import org.anvilpowered.anvil.api.data.key.Key;
import org.anvilpowered.anvil.api.data.key.Keys;

public final class AnvilCoreKeys {

    private AnvilCoreKeys() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static final Key<String> PLUGINS_PERMISSION = new Key<String>("PLUGINS_PERMISSION", "anvil.admin.plugins") {
    };
    public static final Key<String> RELOAD_PERMISSION = new Key<String>("RELOAD_PERMISSION", "anvil.admin.reload") {
    };

    static {
        Keys.registerKey(PLUGINS_PERMISSION);
        Keys.registerKey(RELOAD_PERMISSION);
    }
}
