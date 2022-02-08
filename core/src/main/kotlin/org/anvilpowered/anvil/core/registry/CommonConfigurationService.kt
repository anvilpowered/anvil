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
package org.anvilpowered.anvil.core.registry

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.registry.api.ConfigurationService
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.serialize.TypeSerializerCollection

@Singleton
class CommonConfigurationService  @Inject constructor(
    configLoader: ConfigurationLoader<CommentedConfigurationNode>
) : ConfigurationService(configLoader) {

    init {
        withDataStoreCore()
        withDefault()
        withProxyMode()
        setName(AnvilKeys.REGEDIT_ALLOW_SENSITIVE, "server.regeditAllowSensitive")
        setName(AnvilKeys.TIME_ZONE, "server.timezone")
        setDescription(AnvilKeys.REGEDIT_ALLOW_SENSITIVE, """
Whether the regedit command should have access to sensitive settings such as connection details.
""")
        setDescription(AnvilKeys.TIME_ZONE, """
The server's timezone id. Use "auto" for the local system time, otherwise
please see https://nodatime.org/TimeZones (note that your system's available timezones may differ).
This option is useful if your server machine and community are based in different timezones.
""")
        val serializers = TypeSerializerCollection.defaults().childBuilder()
            .register(AnvilKeys.TIME_ZONE.typeToken, CommonZoneIdSerializer())
            .build()
        this.options = ConfigurationOptions.defaults().serializers(serializers)
    }

    private var isWithDataStore = false

    protected fun withCore() {
        setName(AnvilKeys.SERVER_NAME, "server.name")
        setDescription(AnvilKeys.SERVER_NAME, "\nServer name")
    }

    private fun withDataStoreCore0() {
        setName(AnvilKeys.DATA_DIRECTORY, "datastore.dataDirectory")
        setName(AnvilKeys.DATA_STORE_NAME, "datastore.dataStoreName")
        setDescription(AnvilKeys.DATA_DIRECTORY, """
   Directory for extra data
   Please note that it is not recommended to change this value from the original
   """.trimIndent())
        setDescription(AnvilKeys.DATA_STORE_NAME, "\nDetermines which storage option to use")
    }

    protected fun withDataStoreCore() {
        if (isWithDataStore) return
        isWithDataStore = true
        withDataStoreCore0()
    }

    protected fun withDataStore() {
        if (isWithDataStore) return
        isWithDataStore = true
        withDataStoreCore0()
        setName(AnvilKeys.USE_SHARED_CREDENTIALS, "datastore.anvil.useSharedCredentials")
        setName(AnvilKeys.USE_SHARED_ENVIRONMENT, "datastore.anvil.useSharedEnvironment")
        setDescription(AnvilKeys.USE_SHARED_CREDENTIALS, """
Whether to use Anvil's shared credentials.
If enabled, the following datastore settings will be inherited from Anvil's config (Requires useSharedEnvironment)
	- mongodb.authDb
	- mongodb.connectionString
	- mongodb.password
	- mongodb.username
	- mongodb.useAuth
	- mongodb.useConnectionString
	- mongodb.useSrv
Please note: If this is enabled, the values for above settings in this config file have no effect"""
        )
        setDescription(AnvilKeys.USE_SHARED_ENVIRONMENT, """
Whether to use Anvil's shared environment.
If enabled, the following datastore settings will be inherited from Anvil's config
	- mongodb.hostname
	- mongodb.port
Please note: If this is enabled, the values for above settings in this config file have no effect"""
        )
    }

    protected fun withMongoDB() {
        withDataStore()
        setName(AnvilKeys.MONGODB_CONNECTION_STRING, "datastore.mongodb.connectionString")
        setName(AnvilKeys.MONGODB_HOSTNAME, "datastore.mongodb.hostname")
        setName(AnvilKeys.MONGODB_PORT, "datastore.mongodb.port")
        setName(AnvilKeys.MONGODB_DBNAME, "datastore.mongodb.dbname")
        setName(AnvilKeys.MONGODB_USERNAME, "datastore.mongodb.username")
        setName(AnvilKeys.MONGODB_PASSWORD, "datastore.mongodb.password")
        setName(AnvilKeys.MONGODB_AUTH_DB, "datastore.mongodb.authDb")
        setName(AnvilKeys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth")
        setName(AnvilKeys.MONGODB_USE_SRV, "datastore.mongodb.useSrv")
        setName(AnvilKeys.MONGODB_USE_CONNECTION_STRING, "datastore.mongodb.useConnectionString")
        setDescription(AnvilKeys.MONGODB_CONNECTION_STRING, """
   (Advanced) You will probably not need to use this.
   Custom MongoDB connection string that will used instead of the connection info and credentials below
   Will only be used if useConnectionString=true
   """.trimIndent())
        setDescription(AnvilKeys.MONGODB_HOSTNAME, "\nMongoDB hostname")
        setDescription(AnvilKeys.MONGODB_PORT, "\nMongoDB port")
        setDescription(AnvilKeys.MONGODB_DBNAME, "\nMongoDB database name")
        setDescription(AnvilKeys.MONGODB_USERNAME, "\nMongoDB username")
        setDescription(AnvilKeys.MONGODB_PASSWORD, "\nMongoDB password")
        setDescription(AnvilKeys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication")
        setDescription(AnvilKeys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection")
        setDescription(AnvilKeys.MONGODB_USE_SRV, "\nWhether to interpret the MongoDB hostname as an SRV record")
        setDescription(AnvilKeys.MONGODB_USE_CONNECTION_STRING, """
   (Advanced) You will probably not need to use this.
   Whether to use the connection string provided instead of the normal connection info and credentials
   Only use this if you know what you are doing!
   Please note that this plugin will inherit both useConnectionString and connectionString from
   Anvil if and only if useSharedEnvironment and useSharedCredentials are both true
   """.trimIndent())
    }

    protected fun withRedis() {
        setName(AnvilKeys.REDIS_HOSTNAME, "datastore.redis.hostname")
        setName(AnvilKeys.REDIS_PORT, "datastore.redis.port")
        setName(AnvilKeys.REDIS_PASSWORD, "datastore.redis.password")
        setName(AnvilKeys.REDIS_USE_AUTH, "datastore.redis.useAuth")
        setDescription(AnvilKeys.REDIS_HOSTNAME, "\nRedis hostname")
        setDescription(AnvilKeys.REDIS_PORT, "\nRedis Port")
        setDescription(AnvilKeys.REDIS_PASSWORD, "\nRedis password")
        setDescription(AnvilKeys.REDIS_USE_AUTH, "\nWhether to use authentication (password) for Redis connection")
    }

    protected fun withProxyMode() {
        setName(AnvilKeys.PROXY_MODE, "server.proxyMode")
        setDescription(AnvilKeys.PROXY_MODE, """
   Enable this if your server is running behind a proxy
   If true, this setting disables the join and chat listeners
   to prevent conflicts with the proxy's listeners.
   """.trimIndent())
    }

    protected fun withDefault() {
        withCore()
        withMongoDB()
    }

    protected fun withAll() {
        withDefault()
        withRedis()
        withProxyMode()
    }
}
