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
