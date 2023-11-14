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

@KeyBuilderDsl
interface KeyBuilder<T : Any> {

    /**
     * Sets the fallback value of the generated [Key]
     *
     * @param fallbackValue The fallback value to set
     * @return `this`
     */
    @KeyBuilderDsl
    fun fallback(fallbackValue: T?): KeyBuilder<T>

    /**
     * Sets the description of the generated [Key].
     *
     * @param description The description to set or `null` to remove it
     * @return `this`
     */
    @KeyBuilderDsl
    fun description(description: String?): KeyBuilder<T>

    /**
     * Sets the parser of the generated [Key].
     *
     * @param parser The parser to set or `null` to remove it
     * @return `this`
     */
    @KeyBuilderDsl
    fun parser(parser: ((String) -> T)?): KeyBuilder<T>

    /**
     * Sets the toStringer of the generated [Key].
     *
     * @param toStringer The toStringer to set or `null` to remove it
     * @return `this`
     */
    @KeyBuilderDsl
    fun printer(toStringer: ((T) -> String)?): KeyBuilder<T>

    /**
     * Generates a [Key] based on this builder.
     *
     * @return The generated [Key]
     */
    context(KeyNamespace)
    fun build(): Key<T>
}

@KeyBuilderDsl
interface NamedKeyBuilder<T : Any> : KeyBuilder<T> {
    /**
     * Sets the name of the generated [Key]
     *
     * @param name The name to set
     * @return `this`
     */
    @KeyBuilderDsl
    fun name(name: String): KeyBuilder<T>
}
