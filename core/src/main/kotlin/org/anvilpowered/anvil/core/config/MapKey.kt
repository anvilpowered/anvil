/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.core.config

import io.leangen.geantyref.TypeToken
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json

context(KeyNamespace)
class MapKey<K : Any, V : Any> internal constructor(
    override val type: TypeToken<Map<K, V>>,
    override val name: String,
    override val fallback: Map<K, V>,
    override val description: String?,
    private val keyType: TypeToken<K>,
    private val keySerializer: KSerializer<K>,
    private val valueType: TypeToken<V>,
    private val valueSerializer: KSerializer<V>,
) : Key<Map<K, V>> {

    private val namespace: KeyNamespace = this@KeyNamespace
    private val serializer = MapSerializer(keySerializer, valueSerializer)

    init {
        namespace.add(this)
    }

    fun serializeKey(key: K, json: Json = Json): String = json.encodeToString(keySerializer, key)
    fun deserializeKey(key: String, json: Json = Json): K = json.decodeFromString(keySerializer, key)
    fun serializeValue(value: V, json: Json = Json): String = json.encodeToString(valueSerializer, value)
    fun deserializeValue(value: String, json: Json = Json): V = json.decodeFromString(valueSerializer, value)

    override fun serialize(value: Map<K, V>, json: Json): String = json.encodeToString(serializer, value)
    override fun deserialize(value: String, json: Json): Map<K, V> = json.decodeFromString(serializer, value)

    override fun compareTo(other: Key<Map<K, V>>): Int = Key.comparator.compare(this, other)
    override fun equals(other: Any?): Boolean = (other as Key<*>?)?.let { Key.equals(this, it) } ?: false
    override fun hashCode(): Int = Key.hashCode(this)
    override fun toString(): String = "MapKey<$keyType, $valueType>(name='$name')"

    @KeyBuilderDsl
    interface BuilderFacet<K : Any, V : Any, B : BuilderFacet<K, V, B>> : Key.BuilderFacet<Map<K, V>, MapKey<K, V>, B> {

        /**
         * Sets the key serializer of the generated [Key].
         *
         * This is entirely optional, as the default serializer will be used if this is not set.
         * The default serializer requires the element type to be trivially serializable or annotated with `@Serializable`
         * from the kotlinx-serialization framework.
         *
         * @param serializer The key serializer to set or `null` to use the default
         * @return `this`
         */
        @KeyBuilderDsl
        fun keySerializer(serializer: KSerializer<K>?): B


        /**
         * Sets the value serializer of the generated [Key].
         *
         * This is entirely optional, as the default serializer will be used if this is not set.
         * The default serializer requires the element type to be trivially serializable or annotated with `@Serializable`
         * from the kotlinx-serialization framework.
         *
         * @param serializer The value serializer to set or `null` to use the default
         * @return `this`
         */
        @KeyBuilderDsl
        fun valueSerializer(serializer: KSerializer<V>?): B

    }

    @KeyBuilderDsl
    interface AnonymousBuilderFacet<K : Any, V : Any> :
        BuilderFacet<K, V, AnonymousBuilderFacet<K, V>>,
        Key.BuilderFacet<Map<K, V>, MapKey<K, V>, AnonymousBuilderFacet<K, V>>

    @KeyBuilderDsl
    interface NamedBuilderFacet<K : Any, V : Any> :
        BuilderFacet<K, V, NamedBuilderFacet<K, V>>,
        Key.NamedBuilderFacet<Map<K, V>, MapKey<K, V>, NamedBuilderFacet<K, V>>

    @KeyBuilderDsl
    interface Builder<K : Any, V : Any> :
        BuilderFacet<K, V, Builder<K, V>>,
        Key.Builder<Map<K, V>, MapKey<K, V>, Builder<K, V>>

    @KeyBuilderDsl
    interface FacetedBuilder<K : Any, V : Any> :
        BuilderFacet<K, V, FacetedBuilder<K, V>>,
        Key.FacetedBuilder<Map<K, V>, MapKey<K, V>, FacetedBuilder<K, V>, AnonymousBuilderFacet<K, V>, NamedBuilderFacet<K, V>>
}
