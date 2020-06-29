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

package org.anvilpowered.anvil.api.data.registry;

public enum RegistryScope {

    /**
     * The annotated value persists only between deep reloads.
     * <p>
     * Use this scope for values that should be reloadable but not necessarily
     * during normal operation of the plugin.
     * </p>
     */
    DEEP,

    /**
     * The annotated value persists only between normal reloads.
     * This value is the default value for the {@link RegistryScoped} annotation.
     * <p>
     * Use this scope for
     * </p>
     */
    DEFAULT,
}
