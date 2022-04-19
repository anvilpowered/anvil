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
package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key
import org.anvilpowered.anvil.api.registry.key.Keys
import java.time.ZoneId

object AnvilKeys {
    private const val GLOBAL_NAMESPACE = "global"

    val config = config {
        "server" by {
            "name" by SERVER_NAME describe "\nThe server name"
            "timezone" by TIME_ZONE describe "\nThe time zone"
            "proxy" by PROXY_MODE describe """
                Directory for extra data
                Please note that it is not recommended to change this value from the original
                """.trimIndent()
        }
        "datastore" by {
            "anvil" by {
                "useSharedCredentials" by USE_SHARED_CREDENTIALS describe """
                    Whether to use Anvil's shared credentials.
                    If enabled, the following datastore settings will be inherited from Anvil's config (Requires useSharedEnvironment)
                        - mongodb.authDb
                        - mongodb.connectionString
                        - mongodb.password
                        - mongodb.username
                        - mongodb.useAuth
                        - mongodb.useConnectionString
                        - mongodb.useSrv
                    Please note: If this is enabled, the values for above settings in this config file have no effect""".trimIndent()
                "useSharedEnvironment" by USE_SHARED_ENVIRONMENT describe """
                    Whether to use Anvil's shared environment.
                    If enabled, the following datastore settings will be inherited from Anvil's config
                        - mongodb.hostname
                        - mongodb.port
                    Please note: If this is enabled, the values for above settings in this config file have no effect""".trimIndent()
            }

            "mongodb" by {
                "authDb" by MONGODB_AUTH_DB describe "\nMongoDB database to use for authentication"
                "connectionString" by MONGODB_CONNECTION_STRING describe """
                   (Advanced) You will probably not need to use this.
                   Custom MongoDB connection string that will used instead of the connection info and credentials below
                   Will only be used if useConnectionString=true
                   """.trimIndent()
                "dbname" by MONGODB_DBNAME describe "\nMongoDB database name"
                "hostname" by MONGODB_HOSTNAME describe "\nMongoDB hostname"
                "port" by MONGODB_PORT describe "\nMongoDB port"
                "useAuth" by MONGODB_USE_AUTH describe "\nWhether to use authentication (username/password) for MongoDB"
                "useSrv" by MONGODB_USE_SRV describe "\nWhether to interpret the MongoDB hostname as an SRV record"
            }
        }
    }

    val SERVER_NAME = Key.build {
        name("SERVER_NAME")
        fallback("server")
    }
    val TIME_ZONE = Key.build {
        name("TIME_ZONE")
        fallback(ZoneId.systemDefault())
        parser { input: String? -> ZoneIdSerializer.parse(input) }
        toStringer { zoneId: ZoneId? -> ZoneIdSerializer.toString(zoneId) }
    }
    val PROXY_MODE = Key.build {
        name("PROXY_MODE")
        fallback(false)
    }
    val REGEDIT_ALLOW_SENSITIVE = Key.build {
        name("REGEDIT_ALLOW_SENSITIVE")
        fallback(false)
        userImmutable()
    }
    val BASE_SCAN_PACKAGE = Key.build {
        name("BASE_SCAN_PACKAGE")
        fallback("org.anvilpowered.anvil.common.model")
        userImmutable()
    }
    val CACHE_INVALIDATION_INTERVAL_SECONDS = Key.build {
        name("CACHE_INVALIDATION_INTERVAL_SECONDS")
        fallback(30)
    }
    val CACHE_INVALIDATION_TIMOUT_SECONDS = Key.build {
        name("CACHE_INVALIDATION_TIMOUT_SECONDS")
        fallback(300)
    }
    val USE_SHARED_ENVIRONMENT = Key.build {
        name("USE_SHARED_ENVIRONMENT")
        fallback(false)
        sensitive()
    }
    val USE_SHARED_CREDENTIALS = Key.build {
        name("USE_SHARED_CREDENTIALS")
        fallback(false)
        sensitive()
    }
    val DATA_DIRECTORY: Key<String> = Key.build {
        name("DATA_DIRECTORY")
        fallback("anvil")
        sensitive()
    }
    val DATA_STORE_NAME: Key<String> = Key.build {
        name("DATA_STORE_NAME")
        fallback("xodus")
        sensitive()
    }
    val MONGODB_CONNECTION_STRING: Key<String> = Key.build {
        name("MONGODB_CONNECTION_STRING")
        fallback("mongodb://admin:password@localhost:27017/anvil?authSource=admin")
        sensitive()
    }
    val MONGODB_HOSTNAME = Key.build {
        name("MONGODB_HOSTNAME")
        fallback("localhost")
        sensitive()
    }
    val MONGODB_PORT: Key<Int> = Key.build {
        name("MONGODB_PORT")
        fallback(27017)
        sensitive()
    }
    val MONGODB_DBNAME: Key<String> = Key.build {
        name("MONGODB_DBNAME")
        fallback("anvil")
        sensitive()
    }
    val MONGODB_USERNAME: Key<String> = Key.build {
        name("MONGODB_USERNAME")
        fallback("admin")
        sensitive()
    }
    val MONGODB_PASSWORD: Key<String> = Key.build {
        name("MONGODB_PASSWORD")
        fallback("password")
        sensitive()
    }
    val MONGODB_AUTH_DB: Key<String> = Key.build {
        name("MONGODB_AUTH_DB")
        fallback("admin")
        sensitive()
    }
    val MONGODB_USE_AUTH: Key<Boolean> = Key.build {
        name("MONGODB_USE_AUTH")
        fallback(false)
        sensitive()
    }
    val MONGODB_USE_SRV: Key<Boolean> = Key.build {
        name("MONGODB_USE_SRV")
        fallback(false)
        sensitive()
    }
    val MONGODB_USE_CONNECTION_STRING: Key<Boolean> = Key.build {
        name("MONGODB_USE_CONNECTION_STRING")
        fallback(false)
        sensitive()
    }
    val REDIS_HOSTNAME: Key<String> = Key.build {
        name("REDIS_HOSTNAME")
        fallback("localhost")
        sensitive()
    }
    val REDIS_PORT: Key<Int> = Key.build {
        name("REDIS_PORT")
        fallback(6379)
        sensitive()
    }
    val REDIS_PASSWORD: Key<String> = Key.build {
        name("REDIS_PASSWORD")
        fallback("password")
        sensitive()
    }
    val REDIS_USE_AUTH: Key<Boolean> = Key.build {
        name("REDIS_USE_AUTH")
        fallback(false)
        sensitive()
    }
    val PLUGINS_PERMISSION: Key<String> = Key.build {
        name("PLUGINS_PERMISSION")
        fallback("anvil.admin.plugins")
    }
    val REGEDIT_PERMISSION: Key<String> = Key.build {
        name("REGEDIT_PERMISSION")
        fallback("anvil.admin.regedit")
    }
    val RELOAD_PERMISSION: Key<String> = Key.build {
        name("RELOAD_PERMISSION")
        fallback("anvil.admin.reload")
    }

    init {
        Keys.startRegistration(GLOBAL_NAMESPACE)
            .register(SERVER_NAME)
            .register(TIME_ZONE)
            .register(PROXY_MODE)
            .register(REGEDIT_ALLOW_SENSITIVE)
            .register(BASE_SCAN_PACKAGE)
            .register(CACHE_INVALIDATION_INTERVAL_SECONDS)
            .register(CACHE_INVALIDATION_TIMOUT_SECONDS)
            .register(USE_SHARED_ENVIRONMENT)
            .register(USE_SHARED_CREDENTIALS)
            .register(DATA_DIRECTORY)
            .register(DATA_STORE_NAME)
            .register(MONGODB_HOSTNAME)
            .register(MONGODB_PORT)
            .register(MONGODB_DBNAME)
            .register(MONGODB_USERNAME)
            .register(MONGODB_PASSWORD)
            .register(MONGODB_AUTH_DB)
            .register(MONGODB_USE_AUTH)
            .register(MONGODB_USE_SRV)
            .register(REDIS_HOSTNAME)
            .register(REDIS_PORT)
            .register(REDIS_PASSWORD)
            .register(REDIS_USE_AUTH)
        Keys.startRegistration("anvil")
            .register(PLUGINS_PERMISSION)
            .register(REGEDIT_PERMISSION)
            .register(RELOAD_PERMISSION)
    }
}
