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
package org.anvilpowered.anvil.api.registry.key

import io.leangen.geantyref.TypeToken
import java.util.function.Function

internal class KeyBuilderImpl<T : Any>(type: TypeToken<T>) : Key.Builder<T> {
    private val type: TypeToken<T>
    private var name: String = ""
    private var fallbackValue: T? = null
    private var userImmutable = false
    private var sensitive: Boolean
    private var description: String? = null
    private var parser: Function<String, T>? = null
    private var toStringer: ((T) -> String)? = null

    init {
        this.type = type
        sensitive = false
    }

    override fun name(name: String): Key.Builder<T> {
        this.name = name
        return this
    }

    override fun fallback(fallbackValue: T?): Key.Builder<T> {
        this.fallbackValue = fallbackValue
        return this
    }

    override fun userImmutable(): KeyBuilderImpl<T> {
        userImmutable = true
        return this
    }

    override fun sensitive(): KeyBuilderImpl<T> {
        sensitive = true
        return this
    }

    override fun description(description: String?): KeyBuilderImpl<T> {
        this.description = description
        return this
    }

    override fun parser(parser: Function<String, T>?): KeyBuilderImpl<T> {
        this.parser = parser
        return this
    }

    override fun toStringer(toStringer: ((T) -> String)?): KeyBuilderImpl<T> {
        this.toStringer = toStringer
        return this
    }

    override fun build(): Key<T> {
        return object : Key<T>(
            type,
            name,
            requireNotNull(fallbackValue) { "fallbackValue not set" },
            userImmutable,
            sensitive,
            description,
            parser,
            toStringer ?: Any::toString
        ) {}
    }
}
