package rocks.milspecsg.msrepository.datastore;

import com.google.inject.Key;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

public interface DataStoreConfig {

    String getBaseScanPackage();

    int getDataStoreNameConfigKey();

    Key<ConfigurationService> getConfigurationServiceKey();

}
