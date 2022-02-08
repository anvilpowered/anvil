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
package org.anvilpowered.anvil.api.datastore

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClients
import dev.morphia.Datastore
import dev.morphia.Morphia
import dev.morphia.annotations.Entity
import dev.morphia.mapping.DateStorage
import dev.morphia.mapping.MapperOptions
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.registry.api.Registry
import org.bson.types.ObjectId
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

@Singleton
class MongoContext @Inject constructor(registry: Registry) : DataStoreContext<ObjectId, Datastore>(registry) {

    override fun closeConnection(dataStore: Datastore) {
        dataStore.session?.close()
    }

    override fun loadDataStore(): Datastore {

        /* === Get values from config === */
        val connectionString = registry.getOrDefault(AnvilKeys.MONGODB_CONNECTION_STRING)
        val hostname = registry.getOrDefault(AnvilKeys.MONGODB_HOSTNAME)
        val port = registry.getOrDefault(AnvilKeys.MONGODB_PORT)
        val dbName: String = registry.getOrDefault(AnvilKeys.MONGODB_DBNAME)
        val username = registry.getOrDefault(AnvilKeys.MONGODB_USERNAME)
        val password = registry.getOrDefault(AnvilKeys.MONGODB_PASSWORD)
        val authDb = registry.getOrDefault(AnvilKeys.MONGODB_AUTH_DB)
        val useAuth = registry.getOrDefault(AnvilKeys.MONGODB_USE_AUTH)
        val useSrv = registry.getOrDefault(AnvilKeys.MONGODB_USE_SRV)
        val useConnectionString = registry.getOrDefault(AnvilKeys.MONGODB_USE_CONNECTION_STRING)

        /* === Determine credentials for MongoDB === */
        val clientUrl: String?
        val protocol = if (useSrv) "mongodb+srv://" else "mongodb://"
        val pt = if (useSrv) "" else ":$port"
        if (useConnectionString) {
            clientUrl = connectionString
        } else if (useAuth) {
            var encodedPassword = password
            try {
                encodedPassword = URLEncoder.encode(password, "UTF-8")
            } catch (ignored: UnsupportedEncodingException) {
            }
            clientUrl = "$protocol$username:$encodedPassword@$hostname$pt/$dbName?authSource=$authDb"
        } else {
            clientUrl = "$protocol$hostname$pt/$dbName"
        }

        /* === Establish MongoDB connection === */
        /* === Set class loader to prevent morphia from breaking === */
        val client = MongoClients.create(clientUrl)
        val morphia = Morphia.createDatastore(client, dbName, MapperOptions.legacy()
            .dateStorage(DateStorage.UTC)
            .classLoader(javaClass.classLoader)
            .build())
        morphia.ensureIndexes()

        /* === Save mapped objects and register with morphia === */
        morphia.mapper.map(*calculateEntityClasses(registry.getOrDefault(AnvilKeys.BASE_SCAN_PACKAGE)), Entity::class.java)

        tKeyClass = ObjectId::class.java
        return morphia
    }
}
