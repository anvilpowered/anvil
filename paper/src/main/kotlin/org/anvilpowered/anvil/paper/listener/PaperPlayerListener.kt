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
package org.anvilpowered.anvil.paper.listener

import com.google.inject.Inject
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.entity.RestrictionService
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.AudienceService
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent

class PaperPlayerListener : Listener {

    @Inject
    private lateinit var audienceService: AudienceService<CommandSender>

    @Inject
    private lateinit var coreMemberManager: CoreMemberManager

    @Inject
    private lateinit var registry: Registry

    @Inject
    private lateinit var restrictionService: RestrictionService

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (registry.getOrDefault(Keys.PROXY_MODE) == true) {
            return
        }
        val player = event.player
        coreMemberManager.primaryComponent
            .getOneOrGenerateForUser(
                player.uniqueId,
                player.name,
                player.address?.hostString ?: "invalid"
            )
        audienceService.addToPossible(player)
    }

    @EventHandler
    fun onMovement(event: PlayerMoveEvent) {
        if (restrictionService[event.player.uniqueId].movement()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteraction(event: EntityInteractEvent) {
        if (restrictionService[event.entity.uniqueId].interaction()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventory(event: InventoryInteractEvent) {
        if (restrictionService[event.whoClicked.uniqueId].inventory()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCommands(event: PlayerCommandPreprocessEvent) {
        if (restrictionService[event.player.uniqueId].commands()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onReceiveDamage(event: EntityDamageEvent) {
        if (restrictionService[event.entity.uniqueId].damage()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDealDamage(event: EntityDamageByEntityEvent) {
        if (restrictionService[event.damager.uniqueId].damage()) {
            event.isCancelled = true
        }
    }
}
