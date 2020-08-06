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

package org.anvilpowered.anvil.common.util;

import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CommonUserService<TUser, TPlayer> implements UserService<TUser, TPlayer> {

    protected CoreMemberManager coreMemberManager
        = Anvil.getServiceManager().provide(CoreMemberManager.class);

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(String userName) {
        return coreMemberManager.getPrimaryComponent()
            .getOneForUser(userName).thenApplyAsync(c -> c.map(CoreMember::getUserUUID));
    }

    @Override
    public CompletableFuture<Optional<String>> getUserName(UUID userUUID) {
        return coreMemberManager.getPrimaryComponent()
            .getOneForUser(userUUID).thenApplyAsync(c -> c.map(CoreMember::getUserName));
    }
}
