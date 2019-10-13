package rocks.milspecsg.msrepository.datastore.nitrite;

import com.google.inject.Key;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;

public class NitriteConfig implements DataStoreConfig {

    private String baseScanPackage;
    private int dataStoreNameConfigKey;
    private int userNameConfigKey;
    private int passwordConfigKey;
    private int useAuthConfigKey;
    private int useCompressionConfigKey;
    private Key<ConfigurationService> configurationServiceKey;

    /**
     * @param baseScanPackage         Package to scan for objects to map
     * @param dataStoreNameConfigKey  String configKey
     * @param userNameConfigKey       String configKey
     * @param passwordConfigKey       String configKey
     * @param useAuthConfigKey        Boolean configKey
     * @param useCompressionConfigKey Boolean configKey
     */
    public NitriteConfig(
        String baseScanPackage,
        int dataStoreNameConfigKey,
        int userNameConfigKey,
        int passwordConfigKey,
        int useAuthConfigKey,
        int useCompressionConfigKey
    ) {
        this(
            baseScanPackage,
            dataStoreNameConfigKey,
            userNameConfigKey,
            passwordConfigKey,
            useAuthConfigKey,
            useCompressionConfigKey,
            Key.get(ConfigurationService.class)
        );
    }

    /**
     * @param baseScanPackage         Package to scan for objects to map
     * @param dataStoreNameConfigKey  String configKey
     * @param userNameConfigKey       String configKey
     * @param passwordConfigKey       String configKey
     * @param useAuthConfigKey        Boolean configKey
     * @param useCompressionConfigKey Boolean configKey
     * @param configurationServiceKey Key for configuration service
     */
    public NitriteConfig(
        String baseScanPackage,
        int dataStoreNameConfigKey,
        int userNameConfigKey,
        int passwordConfigKey,
        int useAuthConfigKey,
        int useCompressionConfigKey,
        Key<ConfigurationService> configurationServiceKey
    ) {
        this.baseScanPackage = baseScanPackage;
        this.dataStoreNameConfigKey = dataStoreNameConfigKey;
        this.userNameConfigKey = userNameConfigKey;
        this.passwordConfigKey = passwordConfigKey;
        this.useAuthConfigKey = useAuthConfigKey;
        this.useCompressionConfigKey = useCompressionConfigKey;
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

    public int getUserNameConfigKey() {
        return userNameConfigKey;
    }

    public int getPasswordConfigKey() {
        return passwordConfigKey;
    }

    public int getUseAuthConfigKey() {
        return useAuthConfigKey;
    }

    public int getUseCompressionConfigKey() {
        return useCompressionConfigKey;
    }

    @Override
    public Key<ConfigurationService> getConfigurationServiceKey() {
        return configurationServiceKey;
    }
}
