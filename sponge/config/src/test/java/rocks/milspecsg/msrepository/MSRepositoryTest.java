package rocks.milspecsg.msrepository;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;
import rocks.milspecsg.msrepository.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.service.config.implementation.MSConfigurationService;

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
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Finished"));

        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, injector.getInstance(ConfigurationService.class).getConfigList(ConfigKeys.SOME_LIST)));
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, injector.getInstance(ConfigurationService.class).getConfigMap(ConfigKeys.SOME_MAP)));
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, injector.getInstance(ConfigurationService.class).getConfigMap(ConfigKeys.ANOTHER_MAP)));
    }

    private void initSingletonServices() {
        injector.getInstance(ConfigurationService.class);
    }

    private void initServices() {
        injector = spongeRootInjector.createChildInjector(new MSRepositoryTestModule());
    }

    private class MSRepositoryTestModule extends APIConfigurationModule {
        @Override
        protected void configure() {
            super.configure();
            bind(new TypeLiteral<ApiConfigurationService>() {}).to(new TypeLiteral<MSConfigurationService>() {});
        }
    }
}
