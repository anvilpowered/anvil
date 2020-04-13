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

public interface PermissionService<TSubject> {

    /**
     * Checks whether the provided {@link TSubject} has
     * the provided {@code permission}. Depending on
     * the target platform, this can be a
     * Player, User, Group etc...
     *
     * @param subject    The {@link TSubject} to test
     * @param permission the {@link String} permission to check
     * @return Whether the provided {@link TSubject} has
     * the provided {@code permission}.
     */
    boolean hasPermission(TSubject subject, String permission);
}
