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
