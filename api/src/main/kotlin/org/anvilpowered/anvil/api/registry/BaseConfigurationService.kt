package org.anvilpowered.anvil.api.registry

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.loader.ConfigurationLoader

open class BaseConfigurationService constructor(
    configLoader: ConfigurationLoader<CommentedConfigurationNode>
) : ConfigurationServiceImpl(configLoader) {

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

fun TreeObjectBuilder.withCore() {
    "server" by AnvilKeys.SERVER_NAME describe "\nServer name"
}

fun TreeObjectBuilder.withDataStoreCore() {
    "datastore" by {
        "dataDirectory" by AnvilKeys.DATA_DIRECTORY describe "\nThe directory to store plugin data in"
        "dataStore" by AnvilKeys.DATA_STORE_NAME describe "\nDetermines which storage option to use"
    }
}
