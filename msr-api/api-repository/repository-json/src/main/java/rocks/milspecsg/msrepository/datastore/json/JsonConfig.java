package rocks.milspecsg.msrepository.datastore.json;

import com.google.inject.Key;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;

public class JsonConfig implements DataStoreConfig {

    private String baseScanPackage;
    private int dataStoreNameConfigKey;
    private Key<ConfigurationService> configurationServiceKey;

    /**
     * @param baseScanPackage         Package to scan for objects to map
     * @param dataStoreNameConfigKey  String configKey
     */
    public JsonConfig(
        String baseScanPackage,
        int dataStoreNameConfigKey
    ) {
        this(
            baseScanPackage,
            dataStoreNameConfigKey,
            Key.get(ConfigurationService.class)
        );
    }

    /**
     * @param baseScanPackage         Package to scan for objects to map
     * @param dataStoreNameConfigKey  String configKey
     * @param configurationServiceKey Key for configuration service
     */
    public JsonConfig(
        String baseScanPackage,
        int dataStoreNameConfigKey,
        Key<ConfigurationService> configurationServiceKey
    ) {
        this.baseScanPackage = baseScanPackage;
        this.dataStoreNameConfigKey = dataStoreNameConfigKey;
        this.configurationServiceKey = configurationServiceKey;
    }

    @Override
    public String getBaseScanPackage() {
        return baseScanPackage;
    }

    @Override
    public int getDataStoreNameConfigKey() {
        return dataStoreNameConfigKey;
    }

    @Override
    public Key<ConfigurationService> getConfigurationServiceKey() {
        return configurationServiceKey;
    }

}
