/*
 *   Anvil - AnvilPowered
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

package org.anvilpowered.anvil.api;

import org.anvilpowered.anvil.api.misc.Named;

public interface Platform extends Named {

    /**
     * Returns a lowercase identifier for the current platform
     *
     * <p>
     * Examples: "bungee", "spigot", "sponge", "velocity"
     * </p>
     *
     * @return A lowercase identifier for the current platform
     */
    @Override
    String getName();

    /**
     * @return Whether the current platform is a proxy like bungee or velocity
     */
    boolean isProxy();
}
