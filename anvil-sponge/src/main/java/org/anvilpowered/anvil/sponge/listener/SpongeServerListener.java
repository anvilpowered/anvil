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

package org.anvilpowered.anvil.sponge.listener;

import com.google.inject.Inject;
import org.anvilpowered.anvil.common.anvilnet.CommonPacketBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.service.user.UserStorageService;

public class SpongeServerListener {

    @Inject
    private CommonPacketBus packetBus;

    @Listener
    public void onServerStop(GameStoppingEvent event) {
        System.out.println("In here");
        Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
        packetBus.onServerStop();
    }
}
