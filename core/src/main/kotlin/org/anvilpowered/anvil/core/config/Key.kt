/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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
import kotlin.experimental.ExperimentalTypeInference
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

interface Key<T : Any> : Comparable<Key<T>> {

    val type: TypeToken<T>

    val name: String

    val fallback: T?

    val description: String?

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

    @KeyBuilderDsl
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

    companion object {

        @JvmStatic
        val comparator: Comparator<Key<*>> = Comparator.comparing<Key<*>, String> { it.name }
            .thenComparing(Comparator.comparing { it.type.type.typeName })

        @JvmStatic
        fun equals(a: Key<*>?, b: Key<*>?): Boolean {
            if (a === b) return true
            if (a == null || b == null) return false
            return a.name == b.name && a.type.type.typeName == b.type.type.typeName
        }

        @JvmStatic
        fun hashCode(key: Key<*>): Int {
            var result = key.type.hashCode()
            result = 31 * result + key.name.hashCode()
            return result
        }

        fun <T : Any> simpleBuilder(type: TypeToken<T>): SimpleKey.NamedBuilderFacet<T> = AbstractKeyBuilder(type)
        fun <E : Any> listBuilder(type: TypeToken<E>): ListKey.NamedBuilderFacet<E> = AbstractKeyBuilder(type)
        fun <K : Any, V : Any> mapBuilder(type: TypeToken<T>): ListKey.NamedBuilderFacet<T> = AbstractKeyBuilder(type)

        context(KeyNamespace)
        @OptIn(ExperimentalTypeInference::class)
        inline fun <reified T : Any> build(@BuilderInference block: NamedBuilderFacet<T>.() -> Unit): Key<T> =
            builder(object : TypeToken<T>() {}).apply(block).build()

        context(KeyNamespace)
        @OptIn(ExperimentalTypeInference::class)
        inline fun <reified T : Any> building(
            @BuilderInference crossinline block: BuilderFacet<T>.() -> Unit,
        ): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, Key<T>>> = PropertyDelegateProvider { _, property ->
            val builder = builder(object : TypeToken<T>() {})
            builder.name(property.name)
            builder.block()
            val key = builder.build()
            ReadOnlyProperty { _, _ -> key }
        }

        context(KeyNamespace)
        @OptIn(ExperimentalTypeInference::class)
        inline fun <reified T : Any> buildList(@BuilderInference block: NamedBuilderFacet<T>.() -> Unit): Key<T> =
            builder(object : TypeToken<T>() {}).apply(block).build()

        context(KeyNamespace)
        @OptIn(ExperimentalTypeInference::class)
        inline fun <reified T : Any> buildingList(
            @BuilderInference crossinline block: BuilderFacet<T>.() -> Unit,
        ): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, Key<T>>> = PropertyDelegateProvider { _, property ->
            val builder = builder(object : TypeToken<T>() {})
            builder.name(property.name)
            builder.block()
            val key = builder.build()
            ReadOnlyProperty { _, _ -> key }
        }
    }
}
