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

package org.anvilpowered.anvil.api;

import com.google.common.reflect.TypeToken;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public interface ServiceManager {

    <T> Supplier<T> provideSupplier(TypeToken<T> typeToken);

    <T> Supplier<T> provideSupplier(Class<T> clazz);

    <T> Supplier<T> provideSupplier(String name);

    <T, R> Function<T, R> provideFunction(TypeToken<R> typeToken);

    <T, R> Function<T, R> provideFunction(Class<R> clazz);

    <T, R> Function<T, R> provideFunction(String name);

    <T> T provide(TypeToken<T> typeToken);

    <T> T provide(Class<T> clazz);

    <T> T provide(String name);

    <T, R> R provide(TypeToken<R> typeToken, T input);

    <T, R> R provide(Class<R> clazz, T input);

    <T, R> R provide(String name, T input);

    <T> void registerBinding(TypeToken<T> typeToken, Supplier<T> supplier);

    <T> void registerBinding(Class<T> clazz, Supplier<T> supplier);

    <T, R> void registerBinding(TypeToken<R> typeToken, Function<T, R> function);

    <T, R> void registerBinding(Class<R> clazz, Function<T, R> function);
}
