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

package rocks.milspecsg.msrepository.datastore.json;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.jsondb.JsonDBOperations;
import io.jsondb.JsonDBTemplate;
import io.jsondb.annotation.Document;
import rocks.milspecsg.msrepository.BasicPluginInfo;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.json.annotation.JsonEntity;

import java.nio.file.Paths;
import java.util.UUID;

@Singleton
public class JsonContext extends DataStoreContext<UUID, JsonDBOperations, JsonConfig> {

    @Inject
    private BasicPluginInfo basicPluginInfo;

    @Inject
    public JsonContext(JsonConfig config, Injector injector) {
        super(config, injector);
    }

    @Override
    protected void closeConnection(JsonDBOperations dataStore) {
    }

    @Override
    protected void configLoaded(Object plugin) {
        if (!getConfigurationService().getConfigString(getConfig().getDataStoreNameConfigKey()).equalsIgnoreCase("json")) {
            requestCloseConnection();
            return;
        }

        /* === Initialize storage location === */
        String dbFilesLocation = Paths.get(basicPluginInfo.getId() + "/data/json").toString();

        /* === Initialize JsonDB === */
        JsonDBOperations dataStore = new JsonDBTemplate(dbFilesLocation, getConfig().getBaseScanPackage());
        setDataStore(dataStore);

        /* === Find objects to map === */
        Class<?>[] entityClasses = calculateEntityClasses(getConfig().getBaseScanPackage(), Document.class, JsonEntity.class);

        /* === Create collections if not present === */
        for (Class<?> entityClass : entityClasses) {
            if (entityClass.isAnnotationPresent(Document.class) && !dataStore.collectionExists(entityClass)) {
                dataStore.createCollection(entityClass);
            }
        }

        setTKeyClass(UUID.class);
    }
}
