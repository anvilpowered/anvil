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

context(KeyNamespace)
class Key<T : Any>(
    val type: TypeToken<T>,
    val name: String,
    val description: String? = null,
    private val parser: (String) -> T? = getDefaultParser(type), // TODO: Proper parser interface with parsing exception
    private val printer: (T) -> String = { it.toString() },
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
}

private fun <T> getDefaultParser(type: TypeToken<T>): (String) -> T? {
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
