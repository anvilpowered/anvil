/*
 *   Anvil - MilSpecSG
 *   Copyright (C) 2020 Cableguy20
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

package rocks.milspecsg.anvil.api.datastore;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.mapping.DefaultCreator;
import rocks.milspecsg.anvil.api.Anvil;
import rocks.milspecsg.anvil.api.data.key.Keys;
import rocks.milspecsg.anvil.api.data.registry.Registry;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Singleton
public final class MongoContext extends DataStoreContext<ObjectId, Datastore> {

    @Inject
    public MongoContext(Registry registry) {
        super(registry);
    }

    @Override
    protected void closeConnection(Datastore dataStore) {
        dataStore.getMongo().close();
    }

    @Override
    protected Datastore loadDataStore() {

        /* === Get values from config === */
        String connectionString = Anvil.resolveForSharedEnvironment(Keys.MONGODB_CONNECTION_STRING, registry);
        String hostname = Anvil.resolveForSharedEnvironment(Keys.MONGODB_HOSTNAME, registry);
        int port = Anvil.resolveForSharedEnvironment(Keys.MONGODB_PORT, registry);
        String dbName = Anvil.resolveForSharedEnvironment(Keys.MONGODB_DBNAME, registry);
        String username = Anvil.resolveForSharedEnvironment(Keys.MONGODB_USERNAME, registry);
        String password = Anvil.resolveForSharedEnvironment(Keys.MONGODB_PASSWORD, registry);
        String authDb = Anvil.resolveForSharedEnvironment(Keys.DATA_STORE_NAME, registry);
        boolean useAuth = Anvil.resolveForSharedEnvironment(Keys.MONGODB_USE_AUTH, registry);
        boolean useSrv = Anvil.resolveForSharedEnvironment(Keys.MONGODB_USE_SRV, registry);
        boolean useConnectionString = Anvil.resolveForSharedEnvironment(Keys.MONGODB_USE_CONNECTION_STRING, registry);

        /* === Determine credentials for MongoDB === */
        String clientUrl;
        String protocol = useSrv ? "mongodb+srv://" : "mongodb://";
        String pt = useSrv ? "" : ":" + port;
        if (useConnectionString) {
            clientUrl = connectionString;
        } else if (useAuth) {
            String encodedPassword = password;
            try {
                encodedPassword = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }
            clientUrl = protocol + username + ":" + encodedPassword + "@" + hostname + pt + "/" + dbName + "?authSource=" + authDb;
        } else {
            clientUrl = protocol + hostname + pt + "/" + dbName;
        }

        /* === Establish MongoDB connection === */
        MongoClientURI uri = new MongoClientURI(clientUrl);
        MongoClient mongoClient = new MongoClient(uri);
        Morphia morphia = new Morphia();
        Datastore dataStore = morphia.createDatastore(mongoClient, dbName);
        dataStore.ensureIndexes();

        /* === Save mapped objects and register with morphia === */
        morphia.map(calculateEntityClasses(registry.getOrDefault(Keys.BASE_SCAN_PACKAGE), Entity.class, Embedded.class));

        /* === Set class loader to prevent morphia from breaking === */
        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return MongoContext.this.getClass().getClassLoader();
            }
        });

        setTKeyClass(ObjectId.class);
        return dataStore;
    }
}