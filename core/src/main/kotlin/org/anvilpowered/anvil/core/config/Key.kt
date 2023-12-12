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
import kotlinx.serialization.json.Json
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

interface Key<T : Any> : Comparable<Key<T>> {

    val type: TypeToken<T>

    val name: String

    val fallback: T

    val description: String?

    /**
     * Serializes the given value in a simple [String] representation.
     */
    fun serialize(value: T, json: Json = Json): String

    /**
     * Deserializes the given value from a simple [String] representation.
     */
    fun deserialize(value: String, json: Json = Json): T?

    @KeyBuilderDsl
    interface BuilderFacet<T : Any, K : Key<T>, B : BuilderFacet<T, K, B>> {
        /**
         * Sets the fallback value of the generated [Key]
         *
         * @param fallback The fallback value to set
         * @return `this`
         */
        @KeyBuilderDsl
        fun fallback(fallback: T?): B

        /**
         * Sets the description of the generated [Key].
         *
         * @param description The description to set or `null` to remove it
         * @return `this`
         */
        @KeyBuilderDsl
        fun description(description: String?): B
    }

    @KeyBuilderDsl
    interface NamedBuilderFacet<T : Any, K : Key<T>, B : BuilderFacet<T, K, B>> : BuilderFacet<T, K, B> {
        /**
         * Sets the name of the generated [Key]
         *
         * @param name The name to set
         * @return `this`
         */
        @KeyBuilderDsl
        fun name(name: String): B
    }

    interface Builder<T : Any, K : Key<T>, B : Builder<T, K, B>> : NamedBuilderFacet<T, K, B> {
        /**
         * Generates a [Key] based on this builder.
         *
         * @return The generated [Key]
         */
        context(KeyNamespace)
        @KeyBuilderDsl
        fun build(): K
    }

    @KeyBuilderDsl
    interface FacetedBuilder<
        T : Any, K : Key<T>, B : FacetedBuilder<T, K, B, AF, NF>,
        AF : BuilderFacet<T, K, AF>, NF : NamedBuilderFacet<T, K, NF>,
        > : Builder<T, K, B> {

        /**
         * @return This builder as an (anonymous) [BuilderFacet]
         */
        fun asAnonymousFacet(): AF

        /**
         * @return This builder as a [NamedBuilderFacet]
         */
        fun asNamedFacet(): NF
    }

    companion object {

        val comparator: Comparator<Key<*>> = Comparator.comparing<Key<*>, String> { it.name }
            .thenComparing(Comparator.comparing { it.type.type.typeName })

        fun equals(a: Key<*>?, b: Key<*>?): Boolean {
            if (a === b) return true
            if (a == null || b == null) return false
            return a.name == b.name && a.type.type.typeName == b.type.type.typeName
        }

        fun hashCode(key: Key<*>): Int {
            var result = key.type.hashCode()
            result = 31 * result + key.name.hashCode()
            return result
        }

        context(KeyNamespace)
        inline fun <T : Any> buildSimple(type: TypeToken<T>, block: SimpleKey.NamedBuilderFacet<T>.() -> Unit): SimpleKey<T> {
            val builder = SimpleKeyBuilder(type)
            builder.asNamedFacet().block()
            return builder.build()
        }

        context(KeyNamespace)
        inline fun <T : Any> buildingSimple(
            type: TypeToken<T>,
            crossinline block: SimpleKey.AnonymousBuilderFacet<T>.() -> Unit,
        ): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, SimpleKey<T>>> = PropertyDelegateProvider { _, property ->
            val builder = SimpleKeyBuilder(type)
            builder.name(property.name)
            builder.asAnonymousFacet().block()
            val key = builder.build()
            ReadOnlyProperty { _, _ -> key }
        }

        context(KeyNamespace)
        inline fun <E : Any> buildList(
            elementType: TypeToken<E>,
            block: ListKey.NamedBuilderFacet<E>.() -> Unit,
        ): ListKey<E> {
            val builder = ListKeyBuilder(elementType)
            builder.asNamedFacet().block()
            return builder.build()
        }

        context(KeyNamespace)
        inline fun <E : Any> buildingList(
            elementType: TypeToken<E>,
            crossinline block: ListKey.AnonymousBuilderFacet<E>.() -> Unit,
        ): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, ListKey<E>>> = PropertyDelegateProvider { _, property ->
            val builder = ListKeyBuilder(elementType)
            builder.name(property.name)
            builder.asAnonymousFacet().block()
            val key = builder.build()
            ReadOnlyProperty { _, _ -> key }
        }

        context(KeyNamespace)
        inline fun <K : Any, V : Any> buildMap(
            keyType: TypeToken<K>,
            valueType: TypeToken<V>,
            block: MapKey.NamedBuilderFacet<K, V>.() -> Unit,
        ): MapKey<K, V> {
            val builder = MapKeyBuilder(keyType, valueType)
            builder.asNamedFacet().block()
            return builder.build()
        }

        context(KeyNamespace)
        inline fun <K : Any, V : Any> buildingMap(
            keyType: TypeToken<K>,
            valueType: TypeToken<V>,
            crossinline block: MapKey.AnonymousBuilderFacet<K, V>.() -> Unit,
        ): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, MapKey<K, V>>> = PropertyDelegateProvider { _, property ->
            val builder = MapKeyBuilder(keyType, valueType)
            builder.name(property.name)
            builder.asAnonymousFacet().block()
            val key = builder.build()
            ReadOnlyProperty { _, _ -> key }
        }
    }
}
