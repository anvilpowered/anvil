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
import com.google.inject.Injector
import org.anvilpowered.anvil.api.environment.Environment
import org.anvilpowered.anvil.api.environment.EnvironmentBuilderImpl
import org.anvilpowered.anvil.api.environment.EnvironmentManager
import org.anvilpowered.anvil.api.environment.EnvironmentManagerImpl
import java.util.function.Function
import java.util.function.Supplier

class ServiceManagerImpl : ServiceManager {
    private val supplierBindings: MutableMap<TypeToken<*>, Supplier<*>> = HashMap()
    private val functionBindings: MutableMap<TypeToken<*>, Function<*, *>> = HashMap()
    private lateinit var injector: Injector
    fun setInjector(injector: Injector) {
        this.injector = injector
    }

    override fun <T> provideSupplier(typeToken: TypeToken<T>): Supplier<T> {
        return supplierBindings[typeToken] as Supplier<T>
    }

    override fun <T> provideSupplier(clazz: Class<T>): Supplier<T> {
        return provideSupplier(TypeToken.of(clazz))
    }

    override fun <T> provideSupplier(name: String): Supplier<T> {
        val suppliers: Array<Supplier<T>> = arrayOf()
        for ((key, value) in supplierBindings) {
            if (key.rawType.name.equals(name, ignoreCase = true)) {
                suppliers[0] = value as Supplier<T>
                break
            }
        }
        return suppliers[0]
    }

    override fun <T, R> provideFunction(typeToken: TypeToken<R>): Function<T, R> {
        return functionBindings[typeToken] as Function<T, R>
    }

    override fun <T, R> provideFunction(clazz: Class<R>): Function<T, R> {
        return provideFunction(TypeToken.of(clazz))
    }

    override fun <T, R> provideFunction(name: String): Function<T, R> {
        val functions: Array<Function<T, R>> = arrayOf()
        for ((key, value) in functionBindings) {
            if (key.rawType.name.equals(name, ignoreCase = true)) {
                functions[0] = value as Function<T, R>
                break
            }
        }
        return functions[0]
    }

    override fun <T> provide(typeToken: TypeToken<T>): T {
        return provideSupplier(typeToken).get()
    }

    override fun <T> provide(clazz: Class<T>): T {
        return provideSupplier(clazz).get()
    }

    override fun <T> provide(name: String): T {
        return this.provideSupplier<T>(name).get()
    }

    override fun <T, R> provide(typeToken: TypeToken<R>, input: T): R {
        return provideFunction<Any, R>(typeToken).apply(input!!)
    }

    override fun <T, R> provide(clazz: Class<R>, input: T): R {
        return provideFunction<Any, R>(clazz).apply(input!!)
    }

    override fun <T, R> provide(name: String, input: T): R {
        return this.provideFunction<T, R>(name).apply(input)
    }

    override fun <T> registerBinding(typeToken: TypeToken<T>, supplier: Supplier<T>) {
        supplierBindings[typeToken] = supplier
    }

    override fun <T> registerBinding(clazz: Class<T>, supplier: Supplier<T>) {
        supplierBindings[TypeToken.of(clazz)] = supplier
    }

    override fun <T, R> registerBinding(typeToken: TypeToken<R>, function: Function<T, R>) {
        functionBindings[typeToken] = function
    }

    override fun <T, R> registerBinding(clazz: Class<R>, function: Function<T, R>) {
        functionBindings[TypeToken.of(clazz)] = function
    }

    companion object {
        val environmentManager = EnvironmentManagerImpl()
    }

    init {
        registerBinding<EnvironmentManager>(EnvironmentManager::class.java) { environmentManager }
        registerBinding<Environment.Builder>(Environment.Builder::class.java) { EnvironmentBuilderImpl() }
    }
}