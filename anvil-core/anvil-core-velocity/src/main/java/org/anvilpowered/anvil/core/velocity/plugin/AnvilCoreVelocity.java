/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.core.velocity.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.core.common.plugin.AnvilCore;
import org.anvilpowered.anvil.core.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.core.velocity.listeners.VelocityPlayerListener;
import org.anvilpowered.anvil.core.velocity.module.VelocityModule;
import org.anvilpowered.anvil.velocity.module.ApiVelocityModule;

@Plugin(
    id = AnvilCorePluginInfo.id,
    name = AnvilCorePluginInfo.name,
    version = AnvilCorePluginInfo.version,
    description = AnvilCorePluginInfo.description,
    url = AnvilCorePluginInfo.url,
    authors = "Cableguy20"
)
public class AnvilCoreVelocity extends AnvilCore<PluginContainer> {

    @Inject
    private ProxyServer proxyServer;

    @Inject
    public AnvilCoreVelocity(Injector injector) {
        super(injector, new VelocityModule(), new ApiVelocityModule());
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onInit(ProxyInitializeEvent event) {
        Anvil.completeInitialization();
        proxyServer.getEventManager().register(this, environment.getInjector().getInstance(VelocityPlayerListener.class));
    }
}
