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
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

context(KeyNamespace)
inline fun <T : Any> Key.Companion.buildSimple(type: TypeToken<T>, block: SimpleKey.NamedBuilderFacet<T>.() -> Unit): SimpleKey<T> {
    val builder = SimpleKeyBuilder(type)
    builder.asNamedFacet().block()
    return builder.build()
}

context(KeyNamespace)
inline fun <reified T : Any> Key.Companion.buildSimple(block: SimpleKey.NamedBuilderFacet<T>.() -> Unit): SimpleKey<T> =
    buildSimple(typeTokenOf<T>(), block)

context(KeyNamespace)
inline fun <T : Any> Key.Companion.buildingSimple(
    type: TypeToken<T>,
    crossinline block: SimpleKey.AnonymousBuilderFacet<T>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, SimpleKey<T>>> = PropertyDelegateProvider { _, property ->
    val builder = SimpleKeyBuilder(type)
    builder.name(property.name)
    builder.asAnonymousFacet().block()
    val key = builder.build()
    println("Building simple with type: ${type.type}")
    ReadOnlyProperty { _, _ -> key }
}

context(KeyNamespace)
inline fun <reified T : Any> Key.Companion.buildingSimple(
    crossinline block: SimpleKey.AnonymousBuilderFacet<T>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, SimpleKey<T>>> =
    buildingSimple(typeTokenOf<T>(), block)

context(KeyNamespace)
inline fun <E : Any> Key.Companion.buildList(
    elementType: TypeToken<E>,
    block: ListKey.NamedBuilderFacet<E>.() -> Unit,
): ListKey<E> {
    val builder = ListKeyBuilder(elementType)
    builder.asNamedFacet().block()
    return builder.build()
}

context(KeyNamespace)
inline fun <reified E : Any> Key.Companion.buildList(block: ListKey.NamedBuilderFacet<E>.() -> Unit): ListKey<E> =
    buildList(typeTokenOf<E>(), block)

context(KeyNamespace)
inline fun <E : Any> Key.Companion.buildingList(
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
inline fun <reified E : Any> Key.Companion.buildingList(
    crossinline block: ListKey.AnonymousBuilderFacet<E>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, ListKey<E>>> =
    buildingList(typeTokenOf<E>(), block)

context(KeyNamespace)
inline fun <K : Any, V : Any> Key.Companion.buildMap(
    keyType: TypeToken<K>,
    valueType: TypeToken<V>,
    block: MapKey.NamedBuilderFacet<K, V>.() -> Unit,
): MapKey<K, V> {
    val builder = MapKeyBuilder(keyType, valueType)
    builder.asNamedFacet().block()
    return builder.build()
}

context(KeyNamespace)
inline fun <reified K : Any, reified V : Any> Key.Companion.buildMap(block: MapKey.NamedBuilderFacet<K, V>.() -> Unit): MapKey<K, V> =
    buildMap(typeTokenOf<K>(), typeTokenOf<V>(), block)

context(KeyNamespace)
inline fun <K : Any, V : Any> Key.Companion.buildingMap(
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

context(KeyNamespace)
inline fun <reified K : Any, reified V : Any> Key.Companion.buildingMap(
    crossinline block: MapKey.AnonymousBuilderFacet<K, V>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, MapKey<K, V>>> =
    buildingMap(typeTokenOf<K>(), typeTokenOf<V>(), block)

@PublishedApi
internal inline fun <reified T> typeTokenOf() = object : TypeToken<T>() {}
