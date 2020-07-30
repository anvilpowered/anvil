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

package org.anvilpowered.anvil.sponge.listener;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.core.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.entity.RestrictionService;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.TargetInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class SpongePlayerListener {

    @Inject
    private CoreMemberManager coreMemberManager;

    @Inject
    private Registry registry;

    @Inject
    private RestrictionService restrictionService;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (registry.getOrDefault(Keys.PROXY_MODE)) {
            return;
        }
        Player player = event.getTargetEntity();
        coreMemberManager.getPrimaryComponent()
            .getOneOrGenerateForUser(
                player.getUniqueId(),
                player.getName(),
                player.getConnection().getAddress().getHostString()
            );
    }

    @Listener
    public void onMovement(MoveEntityEvent event, @Getter("getTargetEntity") Entity entity) {
        if (restrictionService.get(entity).movement()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInteraction(InteractEvent event, @Root Entity entity) {
        if (restrictionService.get(entity).interaction()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInventory(TargetInventoryEvent event, @Root Player player) {
        if (event instanceof Cancellable
            && restrictionService.get(player.getUniqueId()).inventory()) {
            ((Cancellable) event).setCancelled(true);
        }
    }

    @Listener
    public void onCommands(SendCommandEvent event, @Root Player player) {
        if (restrictionService.get(player.getUniqueId()).commands()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onDamage(DamageEntityEvent event, @Getter("getTargetEntity") Entity target, @Root DamageSource source) {
        if (restrictionService.get(target).damage() || ((source instanceof EntityDamageSource)
            && restrictionService.get(((EntityDamageSource) source).getSource()).damage())) {
            event.setCancelled(true);
        }
    }
}
