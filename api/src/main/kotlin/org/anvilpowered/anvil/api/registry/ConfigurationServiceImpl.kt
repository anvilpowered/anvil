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

import io.leangen.geantyref.GenericTypeReflector
import io.leangen.geantyref.TypeToken
import org.anvilpowered.anvil.api.registry.key.Key
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.serialize.SerializationException
import java.io.IOException

// TODO: Don't inherit registry, instead provide IO methods for a given registry
class ConfigurationServiceImpl(
    private val loader: ConfigurationLoader<CommentedConfigurationNode>,
) : ConfigurationService {

    private lateinit var rootConfigurationNode: CommentedConfigurationNode

    private val verificationMap = mutableMapOf<Key<*>, Verifier<*>>()
    private val nodeNameMap = mutableMapOf<Key<*>, String>()
    private val nodeDescriptionMap = mutableMapOf<Key<*>, String>()
    var options: ConfigurationOptions? = null

    override suspend fun save() {
        for ((key, value) in nodeNameMap) {
            val node = fromString(value)
            try {
                setNodeValue(node, key)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            loader.save(rootConfigurationNode)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setName(key: Key<*>, name: String) {
        nodeNameMap[key] = name
    }

    fun setDescription(key: Key<*>, description: String) {
        nodeDescriptionMap[key] = description
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setVerification(key: Key<T>, verification: Verifier<T>) {
        verificationMap[key] = verification as Verifier<*>
    }

    private fun fromString(name: String): CommentedConfigurationNode {
        return rootConfigurationNode.node(name.split("."))
    }

    private suspend fun <T : Any> setNodeDefault(node: CommentedConfigurationNode, key: Key<T>) {
        val def = getDefault(key)
        node.set(key.typeToken, def)
        set(key, def)
    }

    private fun <T : Any> setNodeValue(node: CommentedConfigurationNode, key: Key<T>) {
        node.set(key.typeToken.type, getOrDefault(key))
    }

    override suspend fun load() {
        try {
            rootConfigurationNode = if (options == null) {
                loader.load()
            } else {
                loader.load(options)
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
                loader.save(rootConfigurationNode)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun <T : Any> initConfigValue(key: Key<T>, node: CommentedConfigurationNode, modified: BooleanArray): T? {
        return initConfigValue(key, key.typeToken, node, modified)
    }

    /**
     * @param typeToken [TypeToken] of node to parse. Pass a [Key] to save that value to the registry
     * @param node      [CommentedConfigurationNode] to get value from
     */
    private suspend fun <T : Any> initConfigValue(
        key: Key<T>?,
        typeToken: TypeToken<T>,
        node: CommentedConfigurationNode,
        modified: BooleanArray,
    ): T? {
        if (key != null && GenericTypeReflector.isSuperType(key.typeToken.type, MutableList::class.java)) {
            return try {
                val method = MutableList::class.java.getMethod("get", Int::class.javaPrimitiveType)
                val list = node.getList(method.returnType) ?: return null
                val listVerified = verify(verificationMap[key] as Verifier<T>, list as T, node, modified)

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
                    val map: T = verify(verificationMap[key] as Verifier<T>, result as T, node, modified)
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
                set(key, verify(verificationMap[key] as Verifier<T>, value, node, modified) as T)
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
        verificationMap: Verifier<T>?,
        value: T,
        node: CommentedConfigurationNode,
        modified: BooleanArray,
    ): T {
        if (verificationMap == null) return value // if there is no verification function defined
        var result: T = value
        for ((predicate, transformer) in verificationMap) {
            if (predicate(value)) {
                modified[0] = true
                result = transformer(result)
            }
        }
        if (modified[0]) {
            node.set(result)
        }
        return result
    }
}

typealias Verifier<T> = Map<(T) -> Boolean, (T) -> T>
