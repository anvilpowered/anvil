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
import org.jetbrains.annotations.ApiStatus

interface KeyNamespace {
    val name: String

    operator fun <T : Any> get(keyName: String, type: TypeToken<T>): Key<T>?

    @ApiStatus.Internal
    fun <T : Any> add(key: Key<T>)

    companion object {
        fun create(name: String): KeyNamespace {
            return KeyNamespaceImpl(name)
        }
    }
}

internal class KeyNamespaceImpl(override val name: String) : KeyNamespace {
    private val keys: MutableMap<String, Key<*>> = mutableMapOf()

    override fun <T : Any> get(keyName: String, type: TypeToken<T>): Key<T>? {
        val key = keys[keyName] ?: return null
        if (key.type != type) {
            throw TypeCastException("Key $name has type ${key.type} which does not match provided type $type")
        }
        @Suppress("UNCHECKED_CAST")
        return key as Key<T>
    }

    override fun <T : Any> add(key: Key<T>) {
        keys[key.name] = key
    }
}

inline operator fun <reified T : Any> KeyNamespace.get(keyName: String): Key<T>? = get(keyName, TypeToken.get(T::class.java))
