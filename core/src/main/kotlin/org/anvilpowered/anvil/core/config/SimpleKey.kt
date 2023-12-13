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
import kotlinx.serialization.json.Json

context(KeyNamespace)
class SimpleKey<T : Any> internal constructor(
    override val type: TypeToken<T>,
    override val name: String,
    override val fallback: T,
    override val description: String?,
    val serializer: KSerializer<T>,
) : Key<T> {
    private val namespace: KeyNamespace = this@KeyNamespace

    init {
        namespace.add(this)
    }

    override fun serialize(value: T, json: Json): String = json.encodeToString(serializer, value)
    override fun deserialize(value: String, json: Json): T = json.decodeFromString(serializer, value.prepareForDecode(type))

    override fun compareTo(other: Key<T>): Int = Key.comparator.compare(this, other)
    override fun equals(other: Any?): Boolean = (other as Key<*>?)?.let { Key.equals(this, it) } ?: false
    override fun hashCode(): Int = Key.hashCode(this)
    override fun toString(): String = "SimpleKey(type=$type, name='$name')"

    @KeyBuilderDsl
    interface BuilderFacet<T : Any, B : BuilderFacet<T, B>> : Key.BuilderFacet<T, SimpleKey<T>, B> {

        /**
         * Sets the serializer of the generated [Key].
         *
         * This is entirely optional, as the default serializer will be used if this is not set.
         * The default serializer requires the element type to be trivially serializable or annotated with `@Serializable`
         * from the kotlinx-serialization framework.
         *
         * @param serializer The serializer to set or `null` to use the default
         * @return `this`
         */
        @KeyBuilderDsl
        fun serializer(serializer: KSerializer<T>?): B
    }

    @KeyBuilderDsl
    interface AnonymousBuilderFacet<T : Any> :
        BuilderFacet<T, AnonymousBuilderFacet<T>>,
        Key.BuilderFacet<T, SimpleKey<T>, AnonymousBuilderFacet<T>>

    @KeyBuilderDsl
    interface NamedBuilderFacet<T : Any> :
        BuilderFacet<T, NamedBuilderFacet<T>>,
        Key.NamedBuilderFacet<T, SimpleKey<T>, NamedBuilderFacet<T>>

    @KeyBuilderDsl
    interface Builder<T : Any> :
        BuilderFacet<T, Builder<T>>,
        Key.Builder<T, SimpleKey<T>, Builder<T>>

    @KeyBuilderDsl
    interface FacetedBuilder<T : Any> :
        BuilderFacet<T, FacetedBuilder<T>>,
        Key.FacetedBuilder<T, SimpleKey<T>, FacetedBuilder<T>, AnonymousBuilderFacet<T>, NamedBuilderFacet<T>>
}
