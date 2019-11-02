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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import rocks.milspecsg.msrepository.BasicPluginInfo;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.nitrite.annotation.Entity;

import java.nio.file.Paths;
import java.util.Objects;

@Singleton
public final class NitriteContext extends DataStoreContext<NitriteId, Nitrite, NitriteConfig> {

    @Inject
    private BasicPluginInfo basicPluginInfo;

    @Inject
    public NitriteContext(NitriteConfig config, Injector injector) {
        super(config, injector);
    }

    @Override
    public void configLoaded(Object plugin) {
        if (!getConfigurationService().getConfigString(getConfig().getDataStoreNameConfigKey()).equalsIgnoreCase("nitrite")) {
            requestCloseConnection();
            return;
        }

        /* === Get values from config === */
        boolean useCompression = Objects.requireNonNull(getConfigurationService().getConfigBoolean(getConfig().getUseCompressionConfigKey()));
        boolean useAuth = Objects.requireNonNull(getConfigurationService().getConfigBoolean(getConfig().getUseAuthConfigKey()));

        String username = getConfigurationService().getConfigString(getConfig().getUserNameConfigKey());
        String password = getConfigurationService().getConfigString(getConfig().getPasswordConfigKey());
        // nitrite performs null checks

        /* === Initialize storage location === */
        String dbFilesLocation = Paths.get(basicPluginInfo.getId() + "/data/nitrite").toString();

        /* === Initialize Nitrite === */
        NitriteBuilder dbBuilder = Nitrite.builder()
            .filePath(dbFilesLocation + "/nitrite.db");

        if (useCompression) {
            dbBuilder.compressed();
        }

        Nitrite db = useAuth
            ? dbBuilder.openOrCreate(username, password)
            : dbBuilder.openOrCreate();

        setDataStore(db);

        /* === Find objects to map === */
        Class<?>[] entityClasses = calculateEntityClasses(getConfig().getBaseScanPackage(), Entity.class);

        /* === Create collections if not present === */
        for (Class<?> entityClass : entityClasses) {
            if (!Mappable.class.isAssignableFrom(entityClass)) {
                throw new IllegalStateException("Mapped class must extend org.dizitart.no2.mapper.Mappable");
            }
            db.getRepository(entityClass);
        }

        setTKeyClass(NitriteId.class);
    }

    @Override
    protected void closeConnection(Nitrite nitrite) {
        nitrite.close();
    }
}
