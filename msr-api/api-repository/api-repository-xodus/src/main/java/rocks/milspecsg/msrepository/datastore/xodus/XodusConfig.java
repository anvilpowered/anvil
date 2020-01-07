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

package rocks.milspecsg.msrepository.datastore.xodus;

import com.google.inject.Key;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;

public class XodusConfig implements DataStoreConfig {

    private String baseScanPackage;
    private int dataStoreNameConfigKey;
    private Key<ConfigurationService> configurationServiceKey;

    /**
     * @param baseScanPackage         Package to scan for objects to map
     * @param dataStoreNameConfigKey  String configKey
     */
    public XodusConfig(
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
    public XodusConfig(
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
