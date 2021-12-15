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
package org.anvilpowered.anvil.api

import com.google.common.reflect.TypeToken
import java.util.function.Function
import java.util.function.Supplier

interface ServiceManager {
  fun <T> provideSupplier(typeToken: TypeToken<T>?): Supplier<T>?
  fun <T> provideSupplier(clazz: Class<T>?): Supplier<T>?
  fun <T> provideSupplier(name: String?): Supplier<T>?
  fun <T, R> provideFunction(typeToken: TypeToken<R>?): Function<T, R>?
  fun <T, R> provideFunction(clazz: Class<R>?): Function<T, R>?
  fun <T, R> provideFunction(name: String?): Function<T, R>?
  fun <T> provide(typeToken: TypeToken<T>?): T
  fun <T> provide(clazz: Class<T>?): T
  fun <T> provide(name: String?): T
  fun <T, R> provide(typeToken: TypeToken<R>?, input: T): R
  fun <T, R> provide(clazz: Class<R>?, input: T): R
  fun <T, R> provide(name: String?, input: T): R
  fun <T> registerBinding(typeToken: TypeToken<T>?, supplier: Supplier<T>?)
  fun <T> registerBinding(clazz: Class<T>?, supplier: Supplier<T>?)
  fun <T, R> registerBinding(typeToken: TypeToken<R>?, function: Function<T, R>?)
  fun <T, R> registerBinding(clazz: Class<R>?, function: Function<T, R>?)
}
