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
import com.mongodb.MongoClient
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry

@Singleton
class MongoContext @Inject constructor(registry: Registry) : DataStoreContext<ObjectId?, Datastore>(registry) {
  override fun closeConnection(dataStore: Datastore) {
    dataStore.getMongo().close()
  }

  override fun loadDataStore(): Datastore {

    /* === Get values from config === */
    val connectionString = registry.getExtraSafe<String>(Keys.Companion.MONGODB_CONNECTION_STRING)
    val hostname = registry.getExtraSafe<String>(Keys.Companion.MONGODB_HOSTNAME)
    val port = registry.getExtraSafe<Int>(Keys.Companion.MONGODB_PORT)
    val dbName = registry.getExtraSafe<String>(Keys.Companion.MONGODB_DBNAME)
    val username = registry.getExtraSafe<String>(Keys.Companion.MONGODB_USERNAME)
    val password = registry.getExtraSafe<String>(Keys.Companion.MONGODB_PASSWORD)
    val authDb = registry.getExtraSafe<String>(Keys.Companion.MONGODB_AUTH_DB)
    val useAuth = registry.getExtraSafe<Boolean?>(Keys.Companion.MONGODB_USE_AUTH)!!
    val useSrv = registry.getExtraSafe<Boolean?>(Keys.Companion.MONGODB_USE_SRV)!!
    val useConnectionString = registry.getExtraSafe<Boolean?>(Keys.Companion.MONGODB_USE_CONNECTION_STRING)!!

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
    val uri = MongoClientURI(clientUrl)
    val mongoClient = MongoClient(uri)
    val morphia = Morphia()
    val dataStore: Datastore = morphia.createDatastore(mongoClient, dbName)
    dataStore.ensureIndexes()

    /* === Save mapped objects and register with morphia === */morphia.map(calculateEntityClasses(registry.getOrDefault(Keys.Companion.BASE_SCAN_PACKAGE),
      Entity::class.java,
      Embedded::class.java))

    /* === Set class loader to prevent morphia from breaking === */morphia.getMapper().setOptions(MapperOptions.legacy()
      .classLoader(javaClass.classLoader)
      .build())
    tKeyClass = ObjectId::class.java
    return dataStore
  }
}
