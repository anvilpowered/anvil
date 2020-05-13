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

public interface PermissionService {

    /**
     * Checks whether the provided {@code subject} has
     * the provided {@code permission}. Depending on
     * the target platform, this can be a
     * Player, User, Group etc...
     *
     * <p>
     * If the provided {@code subject} is the console, returns {@code true}
     * </p>
     *
     * @param subject    The {@code subject} to test
     * @param permission The {@code permission} to check
     * @return Whether the provided {@code subject} has
     * the provided {@code permission} or is the console.
     */
    boolean hasPermission(Object subject, String permission);
}
