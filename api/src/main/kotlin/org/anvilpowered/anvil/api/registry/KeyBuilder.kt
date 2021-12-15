/*
 *   Anvil - AnvilPowered
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
package org.anvilpowered.anvil.api.registry

import com.google.common.base.Preconditions
import com.google.common.reflect.TypeToken
import org.checkerframework.checker.nullness.qual.MonotonicNonNull
import java.util.function.Function

internal class KeyBuilder<T>(type: TypeToken<T>) : Key.Builder<T?> {
  private val type: TypeToken<T>
  private var name: @MonotonicNonNull String? = null
  private var fallbackValue: T? = null
  private var userImmutable = false
  private var sensitive: Boolean
  private var description: String? = null
  private var parser: Function<String, T>? = null
  private var toStringer: Function<T, String>? = null

  init {
    this.type = Preconditions.checkNotNull(type, "type")
    sensitive = false
  }

  override fun name(name: String): Key.Builder<T?> {
    this.name = Preconditions.checkNotNull(name, "name")
    return this
  }

  override fun fallback(fallbackValue: T?): Key.Builder<T?> {
    this.fallbackValue = fallbackValue
    return this
  }

  override fun userImmutable(): KeyBuilder<T?> {
    userImmutable = true
    return this
  }

  override fun sensitive(): KeyBuilder<T?> {
    sensitive = true
    return this
  }

  override fun description(description: String?): KeyBuilder<T?> {
    this.description = description
    return this
  }

  override fun parser(parser: Function<String, T?>?): KeyBuilder<T?> {
    this.parser = parser
    return this
  }

  override fun toStringer(toStringer: Function<T?, String>?): KeyBuilder<T?> {
    this.toStringer = toStringer
    return this
  }

  override fun build(): Key<T?> {
    Preconditions.checkNotNull(name, "name")
    return object : Key<T>(type, name!!, fallbackValue, userImmutable, sensitive, description, parser, toStringer) {}
  }
}
