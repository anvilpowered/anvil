/*
 *   Anvil - Registry
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

import com.google.inject.Singleton
import io.leangen.geantyref.GenericTypeReflector
import io.leangen.geantyref.TypeToken
import org.anvilpowered.anvil.api.registry.key.Key
import org.anvilpowered.anvil.api.registry.scope.RegistryReloadScope
import org.anvilpowered.anvil.api.registry.scope.RegistryScoped
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.serialize.SerializationException
import java.io.IOException
import java.util.function.Function
import java.util.function.Predicate

@Singleton
open class ConfigurationService constructor(configLoader: ConfigurationLoader<CommentedConfigurationNode>) :
    Registry() {

    private var configLoader: ConfigurationLoader<CommentedConfigurationNode>
    private lateinit var rootConfigurationNode: CommentedConfigurationNode

    private val verificationMap: MutableMap<Key<*>, Map<Predicate<Any>, Function<Any, Any>>>
    private val nodeNameMap: MutableMap<Key<*>, String>
    private val nodeDescriptionMap: MutableMap<Key<*>, String>
    private var configValuesEdited = false
    var options: ConfigurationOptions? = null

    init {
        this.configLoader = configLoader
        verificationMap = hashMapOf()
        nodeNameMap = hashMapOf()
        nodeDescriptionMap = hashMapOf()
    }

    fun save(): Boolean {
        if (configValuesEdited) {
            for ((key, value) in nodeNameMap) {
                val node: CommentedConfigurationNode = fromString(value)
                try {
                    setNodeValue(node, key)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //logger.error("Unable to set config value for $key", e)
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

    fun setName(key: Key<*>, name: String) {
        nodeNameMap[key] = name
    }

    fun setDescription(key: Key<*>, description: String) {
        nodeDescriptionMap[key] = description
    }

    fun <T> setVerification(key: Key<T>, verification: Map<Predicate<T>, Function<T, T>>) {
        verificationMap[key] = verification as Map<Predicate<Any>, Function<Any, Any>>
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

    @RegistryScoped
    override fun load(registryReloadScope: RegistryReloadScope) {
        super.load(registryReloadScope)
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
