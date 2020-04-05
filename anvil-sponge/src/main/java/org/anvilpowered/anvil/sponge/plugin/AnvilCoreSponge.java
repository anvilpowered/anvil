/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.sponge.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.common.plugin.AnvilCore;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.sponge.listener.SpongePlayerListener;
import org.anvilpowered.anvil.sponge.module.ApiSpongeModule;
import org.anvilpowered.anvil.sponge.module.SpongeModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
    id = AnvilCorePluginInfo.id,
    name = AnvilCorePluginInfo.name,
    version = AnvilCorePluginInfo.version,
    description = AnvilCorePluginInfo.description,
    url = AnvilCorePluginInfo.url,
    authors = "Cableguy20"
)
public class AnvilCoreSponge extends AnvilCore<PluginContainer> {

    @Inject
    public AnvilCoreSponge(Injector injector) {
        super(injector, new SpongeModule());
    }

    @Listener(order = Order.EARLY)
    public void onInit(GameInitializationEvent event) {
        AnvilImpl.completeInitialization(new ApiSpongeModule());
        Sponge.getEventManager().registerListeners(this, environment.getInjector().getInstance(SpongePlayerListener.class));
    }
}
