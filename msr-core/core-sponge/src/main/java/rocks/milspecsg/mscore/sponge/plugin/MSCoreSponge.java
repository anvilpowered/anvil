/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.mscore.sponge.plugin;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.mscore.common.plugin.MSCore;
import rocks.milspecsg.mscore.common.plugin.MSCorePluginInfo;
import rocks.milspecsg.mscore.sponge.listeners.SpongePlayerListener;
import rocks.milspecsg.mscore.sponge.module.SpongeModule;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.misc.BindingExtensions;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;
import rocks.milspecsg.msrepository.sponge.module.ApiSpongeModule;

@Plugin(
    id = MSCorePluginInfo.id,
    name = MSCorePluginInfo.name,
    version = MSCorePluginInfo.version,
    description = MSCorePluginInfo.description,
    url = MSCorePluginInfo.url,
    authors = "Cableguy20"
)
public class MSCoreSponge extends MSCore {

    @Inject
    Injector spongeRootInjector;

    @Override
    public String toString() {
        return MSCorePluginInfo.id;
    }

    @Listener(order = Order.EARLY)
    public void onInit(GameInitializationEvent event) {
        injector = spongeRootInjector.createChildInjector(new SpongeModule(), new ApiSpongeModule());
        MSRepository.registerEnvironment("mscore", injector, BindingExtensions.getKey(new TypeToken<PluginInfo<Text>>() {
        }));
        Sponge.getEventManager().registerListeners(this, injector.getInstance(SpongePlayerListener.class));
        load();
    }
}
