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

package org.anvilpowered.anvil.nukkit.util;

import cn.nukkit.permission.Permissible;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NukkitPermissionService implements PermissionService {

    @Override
    public boolean hasPermission(@Nullable Object subject, String permission) {
        return subject instanceof Permissible && ((Permissible) subject).hasPermission(permission);
    }
}
