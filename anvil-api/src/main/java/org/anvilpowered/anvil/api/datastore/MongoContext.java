/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.api.datastore;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.mapping.MapperOptions;
import org.anvilpowered.anvil.api.registry.AnvilKeys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.bson.types.ObjectId;

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
        String connectionString = registry.get(AnvilKeys.INSTANCE.getMONGODB_CONNECTION_STRING());
        String hostname = registry.get(AnvilKeys.INSTANCE.getMONGODB_HOSTNAME());
        int port = registry.get(AnvilKeys.INSTANCE.getMONGODB_PORT());
        String dbName = registry.get(AnvilKeys.INSTANCE.getMONGODB_DBNAME());
        String username = registry.get(AnvilKeys.INSTANCE.getMONGODB_USERNAME());
        String password = registry.get(AnvilKeys.INSTANCE.getMONGODB_PASSWORD());
        String authDb = registry.get(AnvilKeys.INSTANCE.getMONGODB_AUTH_DB());
        boolean useAuth = registry.get(AnvilKeys.INSTANCE.getMONGODB_USE_AUTH());
        boolean useSrv = registry.get(AnvilKeys.INSTANCE.getMONGODB_USE_SRV());
        boolean useConnectionString = registry.get(AnvilKeys.INSTANCE.getMONGODB_USE_CONNECTION_STRING());

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
        morphia.map(calculateEntityClasses(registry.get(AnvilKeys.INSTANCE.getBASE_SCAN_PACKAGE()), Entity.class, Embedded.class));

        /* === Set class loader to prevent morphia from breaking === */
        morphia.getMapper().setOptions(MapperOptions.legacy()
            .classLoader(getClass().getClassLoader())
            .build());

        setTKeyClass(ObjectId.class);
        return dataStore;
    }
}
