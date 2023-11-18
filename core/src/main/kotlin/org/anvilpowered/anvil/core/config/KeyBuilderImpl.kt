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

internal class KeyBuilderImpl<T : Any>(private val type: TypeToken<T>) : NamedKeyBuilder<T> {
    private var name: String = ""
    private var fallbackValue: T? = null
    private var description: String? = null
    private var serializer: ((T) -> String)? = null
    private var deserializer: ((String) -> T)? = null

    override fun name(name: String): KeyBuilderImpl<T> {
        this.name = name
        return this
    }

    override fun fallback(fallbackValue: T?): KeyBuilderImpl<T> {
        this.fallbackValue = fallbackValue
        return this
    }

    override fun description(description: String?): KeyBuilderImpl<T> {
        this.description = description
        return this
    }

    override fun serializer(printer: ((T) -> String)?): KeyBuilderImpl<T> {
        this.serializer = printer
        return this
    }

    override fun deserializer(parser: ((String) -> T)?): KeyBuilderImpl<T> {
        this.deserializer = parser
        return this
    }

    context(KeyNamespace)
    override fun build(): Key<T> = Key(
        type,
        name,
        requireNotNull(fallbackValue) { "fallbackValue not set" },
        description,
        serializer ?: { it.toString() },
        deserializer ?: getDefaultDeserializer(type), // TODO: Proper deserializer interface with parsing exception
    )
}

private fun <T> getDefaultDeserializer(type: TypeToken<T>): (String) -> T? {
    @Suppress("UNCHECKED_CAST")
    return when (type.type) {
        String::class.java -> { it -> it }
        Int::class.java -> { it: String -> it.toIntOrNull() }
        Long::class.java -> { it: String -> it.toLongOrNull() }
        Float::class.java -> { it: String -> it.toFloatOrNull() }
        Double::class.java -> { it: String -> it.toDoubleOrNull() }
        Boolean::class.java -> { it: String -> it.toBooleanStrictOrNull() }
        else -> throw IllegalArgumentException("There is no default parser for $type")
    } as (String) -> T?
}
