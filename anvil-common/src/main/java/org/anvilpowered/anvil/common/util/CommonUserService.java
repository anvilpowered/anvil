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

package org.anvilpowered.anvil.common.util;

import com.google.common.collect.ImmutableList;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public abstract class CommonUserService<TUser, TPlayer> implements UserService<TUser, TPlayer> {

    private static final UUID constant = UUID.nameUUIDFromBytes(new byte[0]);

    private final Class<TUser> userClass;

    protected CommonUserService(Class<TUser> userClass) {
        this.userClass = userClass;
    }

    @Override
    public List<String> matchPlayerNames(String[] context, int index, int length) {
        if (context.length == length) {
            return matchPlayerNames(context[index]);
        }
        return ImmutableList.of();
    }

    @NotNull
    @Override
    public UUID getUUIDSafe(@Nullable Object object) {
        if (userClass.isInstance(object)) {
            return getUUID((TUser) object);
        } else {
            return constant;
        }
    }
}
