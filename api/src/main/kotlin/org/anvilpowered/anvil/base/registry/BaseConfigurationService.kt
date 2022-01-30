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
package org.anvilpowered.anvil.base.registry

import com.google.inject.Inject
import com.google.inject.Singleton
import io.leangen.geantyref.GenericTypeReflector
import io.leangen.geantyref.TypeToken
import org.anvilpowered.anvil.api.registry.ConfigurationService
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.RegistryReloadScope
import org.anvilpowered.anvil.api.registry.RegistryScoped
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.serialize.SerializationException
import java.io.IOException
import java.util.function.Function
import java.util.function.Predicate

/**
 * Service to load and save data from a config file
 *
 * @author Cableguy20
 */
@Suppress("UNCHECKED_CAST")
@Singleton
open class BaseConfigurationService @Inject constructor(configLoader: ConfigurationLoader<CommentedConfigurationNode>) :
    BaseRegistry(), ConfigurationService {
    private var configLoader: ConfigurationLoader<CommentedConfigurationNode>
    private lateinit var rootConfigurationNode: CommentedConfigurationNode

    /**
     * Maps Keys to their verification function
     */
    private val verificationMap: MutableMap<Key<*>, Map<Predicate<Any>, Function<Any, Any>>>

    /**
     * Maps ConfigKeys to configuration node names
     */
    private val nodeNameMap: MutableMap<Key<*>, String>

    /**
     * Maps ConfigKeys to configuration node descriptions
     */
    private val nodeDescriptionMap: MutableMap<Key<*>, String>
    private var configValuesEdited = false
    private var isWithDataStore = false
    private var options: ConfigurationOptions? = null

    init {
        this.configLoader = configLoader
        verificationMap = HashMap()
        nodeNameMap = HashMap()
        nodeDescriptionMap = HashMap()
    }

    protected fun setOptions(options: ConfigurationOptions?) {
        this.options = options
    }

    protected fun withCore() {
        setName(Keys.SERVER_NAME, "server.name")
        setDescription(Keys.SERVER_NAME, "\nServer name")
    }

    private fun withDataStoreCore0() {
        setName(Keys.DATA_DIRECTORY, "datastore.dataDirectory")
        setName(Keys.DATA_STORE_NAME, "datastore.dataStoreName")
        setDescription(Keys.DATA_DIRECTORY, """
   Directory for extra data
   Please note that it is not recommended to change this value from the original
   """.trimIndent())
        setDescription(Keys.DATA_STORE_NAME, "\nDetermines which storage option to use")
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
        setName(Keys.USE_SHARED_CREDENTIALS, "datastore.anvil.useSharedCredentials")
        setName(Keys.USE_SHARED_ENVIRONMENT, "datastore.anvil.useSharedEnvironment")
        setDescription(Keys.USE_SHARED_CREDENTIALS, """
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
        setDescription(Keys.USE_SHARED_ENVIRONMENT, """
Whether to use Anvil's shared environment.
If enabled, the following datastore settings will be inherited from Anvil's config
	- mongodb.hostname
	- mongodb.port
Please note: If this is enabled, the values for above settings in this config file have no effect"""
        )
    }

    protected fun withMongoDB() {
        withDataStore()
        setName(Keys.MONGODB_CONNECTION_STRING, "datastore.mongodb.connectionString")
        setName(Keys.MONGODB_HOSTNAME, "datastore.mongodb.hostname")
        setName(Keys.MONGODB_PORT, "datastore.mongodb.port")
        setName(Keys.MONGODB_DBNAME, "datastore.mongodb.dbname")
        setName(Keys.MONGODB_USERNAME, "datastore.mongodb.username")
        setName(Keys.MONGODB_PASSWORD, "datastore.mongodb.password")
        setName(Keys.MONGODB_AUTH_DB, "datastore.mongodb.authDb")
        setName(Keys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth")
        setName(Keys.MONGODB_USE_SRV, "datastore.mongodb.useSrv")
        setName(Keys.MONGODB_USE_CONNECTION_STRING, "datastore.mongodb.useConnectionString")
        setDescription(Keys.MONGODB_CONNECTION_STRING, """

   (Advanced) You will probably not need to use this.
   Custom MongoDB connection string that will used instead of the connection info and credentials below
   Will only be used if useConnectionString=true
   """.trimIndent())
        setDescription(Keys.MONGODB_HOSTNAME, "\nMongoDB hostname")
        setDescription(Keys.MONGODB_PORT, "\nMongoDB port")
        setDescription(Keys.MONGODB_DBNAME, "\nMongoDB database name")
        setDescription(Keys.MONGODB_USERNAME, "\nMongoDB username")
        setDescription(Keys.MONGODB_PASSWORD, "\nMongoDB password")
        setDescription(Keys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication")
        setDescription(Keys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection")
        setDescription(Keys.MONGODB_USE_SRV, "\nWhether to interpret the MongoDB hostname as an SRV record")
        setDescription(Keys.MONGODB_USE_CONNECTION_STRING, """

   (Advanced) You will probably not need to use this.
   Whether to use the connection string provided instead of the normal connection info and credentials
   Only use this if you know what you are doing!
   Please note that this plugin will inherit both useConnectionString and connectionString from
   Anvil if and only if useSharedEnvironment and useSharedCredentials are both true
   """.trimIndent())
    }

    protected fun withRedis() {
        setName(Keys.REDIS_HOSTNAME, "datastore.redis.hostname")
        setName(Keys.REDIS_PORT, "datastore.redis.port")
        setName(Keys.REDIS_PASSWORD, "datastore.redis.password")
        setName(Keys.REDIS_USE_AUTH, "datastore.redis.useAuth")
        setDescription(Keys.REDIS_HOSTNAME, "\nRedis hostname")
        setDescription(Keys.REDIS_PORT, "\nRedis Port")
        setDescription(Keys.REDIS_PASSWORD, "\nRedis password")
        setDescription(Keys.REDIS_USE_AUTH, "\nWhether to use authentication (password) for Redis connection")
    }

    protected fun withProxyMode() {
        setName(Keys.PROXY_MODE, "server.proxyMode")
        setDescription(Keys.PROXY_MODE, """

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

    protected fun <T> setVerification(
        key: Key<T>,
        verification: Map<Predicate<T>?, Function<T, T>?>,
    ) {
        verificationMap[key] =
            verification as Map<Predicate<Any>, Function<Any, Any>>
    }

    protected fun setName(key: Key<*>, name: String) {
        nodeNameMap[key] = name
    }

    protected fun setDescription(key: Key<*>, description: String) {
        nodeDescriptionMap[key] = description
    }

    override fun <T> set(key: Key<T>, value: T) {
        super.set(key, value)
        configValuesEdited = true
    }

    override fun <T> remove(key: Key<T>) {
        super.remove(key)
        configValuesEdited = true
    }

    override fun <T> transform(key: Key<T>, transformer: (Key<T>, T) -> T) {
        super.transform(key, transformer)
        configValuesEdited = true
    }

    override fun <T> transform(key: Key<T>, transformer: Function<in T, out T>) {
        super.transform(key, transformer)
        configValuesEdited = true
    }

    override fun <T> addToCollection(key: Key<out MutableCollection<T>>, value: T) {
        super.addToCollection(key, value)
        configValuesEdited = true
    }

    override fun <T> removeFromCollection(key: Key<out MutableCollection<T>>, value: T) {
        super.removeFromCollection(key, value)
        configValuesEdited = true
    }

    override fun <K, T> putInMap(key: Key<out MutableMap<K, T>>, mapKey: K, mapValue: T) {
        super.putInMap(key, mapKey, mapValue)
        configValuesEdited = true
    }

    override fun <K, T> removeFromMap(key: Key<out MutableMap<K, T>>, mapKey: K) {
        super.removeFromMap(key, mapKey)
        configValuesEdited = true
    }

    override fun load(registryReloadScope: RegistryReloadScope) {
        val ordinal: Int = registryReloadScope.ordinal
        if (ordinal <= RegistryReloadScope.DEFAULT.ordinal) {
            loadDefaultScope()
        }
        loadConfig()
        loadOrdinal(ordinal)
    }

    override fun save(): Boolean {
        if (configValuesEdited) {
            for ((key, value) in nodeNameMap) {
                val node: CommentedConfigurationNode = fromString(value)
                try {
                    setNodeValue(node, key)
                } catch (e: Exception) {
                    logger.error("Unable to set config value for $key", e)
                }
            }
            try {
                configLoader.save(rootConfigurationNode)
                configValuesEdited = false
                return true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun fromString(name: String): CommentedConfigurationNode {
        val path = name.split(".").toTypedArray()
        return rootConfigurationNode.node(*path)
    }

    private fun <T> setNodeDefault(node: CommentedConfigurationNode, key: Key<T>) {
        val def = getDefault(key)
        node.set(key.typeToken, def)
        set(key, def)
    }

    private fun <T> setNodeValue(node: CommentedConfigurationNode, key: Key<T>) {
        node.set(key.typeToken.type, getOrDefault(key))
    }

    @RegistryScoped
    private fun loadConfig() {
        try {
            rootConfigurationNode = if (options == null) {
                configLoader.load()
            } else {
                configLoader.load(options)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var updatedCount = 0
        for ((key, value) in nodeNameMap) {
            val node: CommentedConfigurationNode = fromString(value)
            if (node.virtual()) {
                try {
                    setNodeDefault(node, key)
                    updatedCount++
                } catch (e: SerializationException) {
                    e.printStackTrace()
                }
            } else {
                val modified = booleanArrayOf(false)
                initConfigValue(key, node, modified)
                if (modified[0]) {
                    updatedCount++
                }
            }
            if (node.virtual() || node.comment().isNullOrBlank()) {
                node.comment(nodeDescriptionMap[key])
                updatedCount++
            }
        }
        if (updatedCount > 0) {
            try {
                configLoader.save(rootConfigurationNode)
                configValuesEdited = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun <T> initConfigValue(key: Key<T>, node: CommentedConfigurationNode, modified: BooleanArray): T? {
        return initConfigValue(key, key.typeToken, node, modified)
    }

    /**
     * @param typeToken [TypeToken] of node to parse. Pass a [Key] to save that value to the registry
     * @param node      [CommentedConfigurationNode] to get value from
     */
    private fun <T> initConfigValue(
        key: Key<T>?,
        typeToken: TypeToken<T>,
        node: CommentedConfigurationNode,
        modified: BooleanArray,
    ): T? {
        if (key != null && GenericTypeReflector.isSuperType(key.typeToken.type, MutableList::class.java)) {
            return try {
                val method = MutableList::class.java.getMethod("get", Int::class.javaPrimitiveType)
                val list = node.getList(method.returnType) ?: return null
                val listVerified = verify(verificationMap[key], list, node, modified) as T

                set(key, listVerified)
                listVerified
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else if (GenericTypeReflector.isSuperType(MutableMap::class.java, typeToken.type)) {
            try {
                val method = MutableMap::class.java.getMethod("get", Object::class.java)
                val subType = TypeToken.get(method.returnType)
                val result: MutableMap<Any, Any> = hashMapOf()

                for (entry: Map.Entry<*, CommentedConfigurationNode?> in node.childrenMap().entries) {
                    // here comes the recursion
                    val entryKey = entry.value?.key() ?: return null
                    result[entryKey] = initConfigValue(null, subType, entry.value!!, modified)!!
                }

                if (key != null) {
                    val map: T = verify(verificationMap[key], result, node, modified) as (T)
                    set(key, map)
                }
                return result as T
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        } else if (key != null) {
            return try {
                val value = node.get(typeToken) ?: return null
                set(key, verify(verificationMap[key], value, node, modified) as T)
                value
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            return try {
                node.get(typeToken)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun <T> verify(
        verificationMap: Map<Predicate<T>, Function<T, T>>?,
        value: T,
        node: CommentedConfigurationNode,
        modified: BooleanArray,
    ): T {
        if (verificationMap == null) return value // if there is no verification function defined
        var result = value
        for ((key, value1) in verificationMap) {
            if (key.test(result)) {
                modified[0] = true
                result = value1.apply(result)
            }
        }
        if (modified[0]) {
            node.set(result)
        }
        return result
    }
}
