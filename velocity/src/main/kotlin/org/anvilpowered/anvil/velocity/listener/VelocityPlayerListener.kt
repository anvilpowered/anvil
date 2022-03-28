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
package org.anvilpowered.anvil.velocity.listener

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.anvil.api.util.AudienceService
import org.anvilpowered.anvil.api.registry.Registry

class VelocityPlayerListener @Inject constructor(
    private val audienceService: AudienceService<CommandSource>,
    private val registry: Registry
) {

    @Subscribe
    fun onPlayerJoin(event: LoginEvent) {
        if (registry.getOrDefault(AnvilKeys.PROXY_MODE)) {
            return
        }
        audienceService.addToPossible(event.player)
    }
}
