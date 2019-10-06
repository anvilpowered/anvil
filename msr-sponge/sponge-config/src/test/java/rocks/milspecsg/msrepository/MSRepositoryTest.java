/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.commands.TestCommand;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;
import rocks.milspecsg.msrepository.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.service.config.implementation.MSConfigurationService;

import java.util.List;
import java.util.Map;

@Plugin(id = PluginInfo.Id, name = PluginInfo.Name, version = PluginInfo.Version, description = PluginInfo.Description, authors = PluginInfo.Authors, url = PluginInfo.Url)
public class MSRepositoryTest {
    @Inject
    private Logger logger;

    @Inject
    public Injector spongeRootInjector;

    public static MSRepositoryTest plugin = null;
    private Injector injector = null;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }



    @Listener
    public void onServerInitialization(GameInitializationEvent event) {
        plugin = this;
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Loading..."));
        initServices();
        initSingletonServices();


        CommandSpec mainCommand = CommandSpec.builder()
            .description(Text.of("Test command"))
            .executor(injector.getInstance(TestCommand.class))
            .build();

        Sponge.getCommandManager().register(plugin, mainCommand, "msrepositorytest", "test");


        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Finished"));


        // tests

        ConfigurationService configurationService = injector.getInstance(ConfigurationService.class);



        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, configurationService.getConfigList(ConfigKeys.SOME_LIST, new TypeToken<List<Integer>>() {})));
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, configurationService.getConfigMap(ConfigKeys.SOME_MAP, new TypeToken<Map<String, Map<String, Integer>>>() {})));
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, configurationService.getConfigMap(ConfigKeys.ANOTHER_MAP, new TypeToken<Map<Integer, List<String>>>() {})));

    }

    private void initSingletonServices() {
        injector.getInstance(ConfigurationService.class).load(this);
    }

    private void initServices() {
        injector = spongeRootInjector.createChildInjector(new MSRepositoryTestModule());
    }

    private static class MSRepositoryTestModule extends APIConfigurationModule {
        @Override
        protected void configure() {
            super.configure();
            bind(new TypeLiteral<ApiConfigurationService>() {}).to(new TypeLiteral<MSConfigurationService>() {});
            bind(TestCommand.class);
        }
    }
}
