package rocks.milspecsg.msrepository;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

public class APIConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ConfigurationService>() {
        }).to(new TypeLiteral<ApiConfigurationService>() {
        });
    }
}
