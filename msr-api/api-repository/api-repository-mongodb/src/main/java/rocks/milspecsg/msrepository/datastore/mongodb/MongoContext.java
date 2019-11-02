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

package rocks.milspecsg.msrepository.datastore.mongodb;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.mapping.DefaultCreator;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

@Singleton
public final class MongoContext extends DataStoreContext<ObjectId, Datastore, MongoConfig> {

    @Inject
    public MongoContext(MongoConfig config, Injector injector) {
        super(config, injector);
    }

    @Override
    protected void closeConnection(Datastore dataStore) {
        dataStore.getMongo().close();
    }

    @Override
    protected void configLoaded(Object plugin) {
        if (!getConfigurationService().getConfigString(getConfig().getDataStoreNameConfigKey()).equalsIgnoreCase("mongodb")) {
            requestCloseConnection();
            return;
        }

        /* === Get values from config === */
        String hostname = Objects.requireNonNull(getConfigurationService().getConfigString(getConfig().getHostNameConfigKey()));
        int port = Objects.requireNonNull(getConfigurationService().getConfigInteger(getConfig().getPortConfigKey()));
        String dbName = Objects.requireNonNull(getConfigurationService().getConfigString(getConfig().getDbNameConfigKey()));
        boolean useAuth = Objects.requireNonNull(getConfigurationService().getConfigBoolean(getConfig().getUseAuthConfigKey()));

        String username = getConfigurationService().getConfigString(getConfig().getUserNameConfigKey());
        String password = getConfigurationService().getConfigString(getConfig().getPasswordConfigKey());

        if (useAuth) {
            Objects.requireNonNull(username);
            Objects.requireNonNull(password);
        }

        /* === Determine credentials for MongoDB === */
        String clientUrl;
        if (useAuth) {
            String encodedPassword = password;
            try {
                encodedPassword = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }
            clientUrl = "mongodb://" + username + ":" + encodedPassword + "@" + hostname + ":" + port + "/" + dbName;
        } else {
            clientUrl = "mongodb://" + hostname + ":" + port + "/" + dbName;
        }

        /* === Establish MongoDB connection === */
        MongoClientURI uri = new MongoClientURI(clientUrl);
        MongoClient mongoClient = new MongoClient(uri);
        Morphia morphia = new Morphia();
        Datastore dataStore = morphia.createDatastore(mongoClient, dbName);
        dataStore.ensureIndexes();
        setDataStore(dataStore);

        /* === Save mapped objects and register with morphia === */
        morphia.map(calculateEntityClasses(getConfig().getBaseScanPackage(), Entity.class, Embedded.class));

        /* === Set class loader to prevent morphia from breaking === */
        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return MongoContext.this.getClass().getClassLoader();
            }
        });

        setTKeyClass(ObjectId.class);
    }
}