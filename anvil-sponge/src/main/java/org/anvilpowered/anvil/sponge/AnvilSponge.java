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

package org.anvilpowered.anvil.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl;
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo;
import org.anvilpowered.anvil.sponge.listener.SpongePlayerListener;
import org.anvilpowered.anvil.sponge.listener.SpongeServerListener;
import org.anvilpowered.anvil.sponge.module.ApiSpongeModule;
import org.anvilpowered.anvil.sponge.module.SpongeFallbackModule;
import org.anvilpowered.anvil.sponge.module.SpongeModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
    id = AnvilPluginInfo.id,
    name = AnvilPluginInfo.name,
    version = AnvilPluginInfo.version,
    description = AnvilPluginInfo.description,
    url = AnvilPluginInfo.url,
    authors = AnvilPluginInfo.organizationName
)
public class AnvilSponge extends AnvilImpl {

    @Inject
    public AnvilSponge(Injector injector) {
        super(injector, new SpongeModule());
    }

    @Listener(order = Order.EARLY)
    public void onInit(GameInitializationEvent event) {
        EnvironmentBuilderImpl.completeInitialization(new ApiSpongeModule(), new SpongeFallbackModule());
        Sponge.getEventManager().registerListeners(this,
            environment.getInjector().getInstance(SpongePlayerListener.class));
        Sponge.getEventManager().registerListeners(this,
            environment.getInjector().getInstance(SpongeServerListener.class));
    }
}
