/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.core.sponge.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import rocks.milspecsg.anvil.api.Anvil;
import rocks.milspecsg.anvil.core.common.plugin.AnvilCore;
import rocks.milspecsg.anvil.core.common.plugin.AnvilCorePluginInfo;
import rocks.milspecsg.anvil.core.sponge.listeners.SpongePlayerListener;
import rocks.milspecsg.anvil.core.sponge.module.SpongeModule;
import rocks.milspecsg.anvil.sponge.module.ApiSpongeModule;

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
        super(injector, new SpongeModule(), new ApiSpongeModule());
    }

    @Listener(order = Order.EARLY)
    public void onInit(GameInitializationEvent event) {
        Anvil.completeInitialization();
        Sponge.getEventManager().registerListeners(this, environment.getInjector().getInstance(SpongePlayerListener.class));
    }
}
