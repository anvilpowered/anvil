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

context(KeyNamespace)
class Key<T : Any> internal constructor(
    val type: TypeToken<T>,
    val name: String,
    val fallback: T,
    val description: String?,
    private val parser: (String) -> T?,
    private val printer: (T) -> String,
) : Comparable<Key<T>> {

    val namespace: KeyNamespace = this@KeyNamespace

    init {
        namespace.add(this)
    }

    private val comparator = Comparator.comparing<Key<T>, String> { it.name }
        .thenComparing(Comparator.comparing { it.type.type.typeName })

    fun parse(value: String): T? = parser(value)
    fun print(value: T): String = printer(value)

    override fun compareTo(other: Key<T>): Int = comparator.compare(this, other)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Key<*>) return false
        return name == other.name && type.type == other.type.type
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String = "Key(type=$type, name=$name, description=$description)"

    companion object {
        fun <T : Any> builder(type: TypeToken<T>): NamedKeyBuilder<T> = KeyBuilderImpl(type)

        context(KeyNamespace)
        @OptIn(ExperimentalTypeInference::class)
        inline fun <reified T : Any> build(@BuilderInference block: NamedKeyBuilder<T>.() -> Unit): Key<T> {
            return builder(object : TypeToken<T>() {}).apply(block).build()
        }

        context(KeyNamespace)
        @OptIn(ExperimentalTypeInference::class)
        inline fun <reified T : Any> building(
            @BuilderInference crossinline block: KeyBuilder<T>.() -> Unit,
        ): PropertyDelegateProvider<KeyNamespace, ReadOnlyProperty<KeyNamespace, Key<T>>> = PropertyDelegateProvider { _, property ->
            val builder = builder(object : TypeToken<T>() {})
            builder.name(property.name)
            builder.block()
            val key = builder.build()
            ReadOnlyProperty { _, _ -> key }
        }
    }
}
