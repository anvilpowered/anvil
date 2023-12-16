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

/**
 * Builds a [SimpleKey] with the given [type] and configuration [block].
 * The [Key.name] must be set manually via [Key.NamedBuilderFacet.name],
 * but the generic type is inferred by the value provided to [Key.BuilderFacet.fallback].
 *
 * Example:
 * ```
 * val DB_USER = Key.buildSimple {
 *     name("DB_USER")
 *     fallback("foobar")
 * }
 * ```
 */
context(KeyNamespace)
inline fun <reified T : Any> Key.Companion.buildSimple(
    type: TypeToken<T> = typeTokenOf(),
    block: SimpleKey.NamedBuilderFacet<T>.() -> Unit,
): SimpleKey<T> {
    val builder = SimpleKeyBuilder(type)
    builder.asNamedFacet().block()
    return builder.build()
}

/**
 * Builds a [SimpleKey] property delegate with the given [type] and configuration [block].
 * The [Key.name] is set to the name of the property automatically,
 * and the generic type is inferred by the value provided to [Key.BuilderFacet.fallback].
 *
 * Example:
 * ```
 * val DB_USER by Key.buildingSimple {
 *     fallback("foobar")
 * }
 * ```
 */
context(KeyNamespace)
inline fun <reified T : Any> Key.Companion.buildingSimple(
    type: TypeToken<T> = typeTokenOf(),
    crossinline block: SimpleKey.AnonymousBuilderFacet<T>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, SimpleKey<T>>> = PropertyDelegateProvider { _, property ->
    val builder = SimpleKeyBuilder(type)
    builder.name(property.name)
    builder.asAnonymousFacet().block()
    val key = builder.build()
    ReadOnlyProperty { _, _ -> key }
}

/**
 * Builds a [ListKey] with the given [elementType] and configuration [block].
 * The [Key.name] must be set manually via [Key.NamedBuilderFacet.name],
 * but the generic type is inferred by the value provided to [Key.BuilderFacet.fallback].
 *
 * Example:
 * ```
 * val LOG_COMMMANDS = Key.buildList {
 *     name("LOG_COMMANDS")
 *     description("Commands to log")
 *     fallback(listOf("kick", "ban"))
 * }
 * ```
 */
context(KeyNamespace)
inline fun <reified E : Any> Key.Companion.buildList(
    elementType: TypeToken<E> = typeTokenOf(),
    block: ListKey.NamedBuilderFacet<E>.() -> Unit,
): ListKey<E> {
    val builder = ListKeyBuilder(elementType)
    builder.asNamedFacet().block()
    return builder.build()
}

/**
 * Builds a [ListKey] property delegate with the given [elementType] and configuration [block].
 * The [Key.name] is set to the name of the property automatically,
 * and the generic type is inferred by the value provided to [Key.BuilderFacet.fallback].
 *
 * Example:
 * ```
 * val LOG_COMMANDS by Key.buildingList {
 *     description("Commands to log")
 *     fallback(listOf("kick", "ban"))
 * }
 * ```
 */
context(KeyNamespace)
inline fun <reified E : Any> Key.Companion.buildingList(
    elementType: TypeToken<E> = typeTokenOf(),
    crossinline block: ListKey.AnonymousBuilderFacet<E>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, ListKey<E>>> = PropertyDelegateProvider { _, property ->
    val builder = ListKeyBuilder(elementType)
    builder.name(property.name)
    builder.asAnonymousFacet().block()
    val key = builder.build()
    ReadOnlyProperty { _, _ -> key }
}

/**
 * Builds a [MapKey] with the given [keyType], [valueType] and configuration [block].
 * The [Key.name] must be set manually via [Key.NamedBuilderFacet.name],
 * but the generic type is inferred by the value provided to [Key.BuilderFacet.fallback].
 *
 * Example:
 * ```
 * val RANKUP_COMMANDS = Key.buildMap {
 *     name("RANKUP_COMMANDS")
 *     description("Commands to execute when a player ranks up to a certain rank")
 *     fallback(mapOf<String, List<String>>())
 * }
 * ```
 */
context(KeyNamespace)
inline fun <reified K : Any, reified V : Any> Key.Companion.buildMap(
    keyType: TypeToken<K> = typeTokenOf(),
    valueType: TypeToken<V> = typeTokenOf(),
    block: MapKey.NamedBuilderFacet<K, V>.() -> Unit,
): MapKey<K, V> {
    val builder = MapKeyBuilder(keyType, valueType)
    builder.asNamedFacet().block()
    return builder.build()
}

/**
 * Builds a [MapKey] property delegate with the given [keyType], [valueType] and configuration [block].
 * The [Key.name] is set to the name of the property automatically,
 * and the generic type is inferred by the value provided to [Key.BuilderFacet.fallback].
 *
 * Example:
 * ```
 * val RANKUP_COMMANDS by Key.buildingMap {
 *     description("Commands to execute when a player ranks up to a certain rank")
 *     fallback(mapOf<String, List<String>>())
 * }
 * ```
 */
context(KeyNamespace)
inline fun <reified K : Any, reified V : Any> Key.Companion.buildingMap(
    keyType: TypeToken<K> = typeTokenOf(),
    valueType: TypeToken<V> = typeTokenOf(),
    crossinline block: MapKey.AnonymousBuilderFacet<K, V>.() -> Unit,
): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, MapKey<K, V>>> = PropertyDelegateProvider { _, property ->
    val builder = MapKeyBuilder(keyType, valueType)
    builder.name(property.name)
    builder.asAnonymousFacet().block()
    val key = builder.build()
    ReadOnlyProperty { _, _ -> key }
}

@PublishedApi
internal inline fun <reified T> typeTokenOf() = object : TypeToken<T>() {}
