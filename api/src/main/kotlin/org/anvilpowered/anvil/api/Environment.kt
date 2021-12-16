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

import com.google.common.base.Preconditions
import com.google.common.reflect.TypeToken
import com.google.inject.Binding
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.Provider
import org.anvilpowered.anvil.api.plugin.PluginInfo
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.misc.Named
import org.anvilpowered.anvil.api.registry.Registry
import java.util.function.Consumer
import java.util.function.Supplier

interface Environment : Named, Comparable<Environment> {

  fun <T> getBinding(name: String): Binding<T> = getBinding(name, injector)
  fun <T> getKey(name: String): Key<T> = getKey(name, injector)
  fun <T> getProvider(name: String): Provider<T> = getProvider(name, injector)
  fun <T> getInstance(name: String): T = getInstance(name, injector)

  fun reload()
  val injector: Injector
  val plugin: Any
  val pluginInfo: PluginInfo
  val registry: Registry

  interface Builder {
    fun addModules(vararg modules: Module): Builder
    fun addModules(modules: Iterable<Module>): Builder
    fun addEarlyServices(vararg keys: Key<*>): Builder
    fun addEarlyServices(keys: Iterable<Key<*>>): Builder
    fun addEarlyServices(vararg classes: Class<*>): Builder
    fun addEarlyServices(vararg typeLiterals: TypeLiteral<*>): Builder
    fun addEarlyServices(vararg typeTokens: TypeToken<*>): Builder
    fun <T> addEarlyServices(key: Key<T>, initializer: Consumer<T>): Builder
    fun <T> addEarlyServices(clazz: Class<T>, initializer: Consumer<T>): Builder
    fun <T> addEarlyServices(typeLiteral: TypeLiteral<T>, initializer: Consumer<T>): Builder
    fun <T> addEarlyServices(typeToken: TypeToken<T>, initializer: Consumer<T>): Builder

    /**
     * Sets the name for this environment builder.
     *
     * @param name [String] Name to set.
     * @return `this`
     */
    fun setName(name: String): Builder

    /**
     * Sets the root injector for this environment builder.
     *
     * @param rootInjector [Injector] to set. Pass `null` to unset.
     * @return `this`
     */
    fun setRootInjector(rootInjector: Injector): Builder

    /**
     * Sets the logger, only when you are not using SLF4j or JavaUtil
     *
     *
     *
     * The provided logger is adapted to the [org.slf4j.Logger].
     * If no logger is provided, Anvil's logger will be used.
     * (This means logs will be prefixed with "Anvil" instead of your plugin name)
     *
     *
     * <pre>`
     * setLoggerSupplier(MyPlugin.this::getLogger);
    `</pre> *
     *
     * @param logger to set.
     */
    fun setLoggerSupplier(logger: Supplier<*>): Builder

    /**
     * Called when the [Environment] is loaded.
     *
     *
     *
     * This [Consumer] will be invoked when the [Environment]
     * is first loaded and on subsequent reloads.
     *
     *
     *
     *
     * This method can be called multiple times on one builder.
     * Preexisting listeners will be used and will not be overridden.
     *
     *
     * @param listener [Consumer] to run when this environment is loaded
     * @return `this`
     */
    fun whenLoaded(listener: Consumer<Environment>): Builder

    /**
     * Called when the [Environment] is loaded for the first time.
     *
     *
     *
     * This [Consumer] will only be invoked when the [Environment]
     * is loaded for the first time.
     *
     *
     *
     *
     * This method can be called multiple times on one builder.
     * Preexisting listeners will be used and will not be overridden.
     *
     *
     * @param listener [Consumer] to run when this environment is ready
     * @return `this`
     */
    fun whenReady(listener: Consumer<Environment>): Builder

    /**
     * Called when the [Environment] is reloaded.
     *
     *
     *
     * This [Consumer] will only be invoked when the [Environment]
     * is reloaded, but not when it is first loaded.
     *
     *
     *
     *
     * This method can be called multiple times on one builder.
     * Preexisting listeners will be used and will not be overridden.
     *
     *
     * @param listener [Consumer] to run when this environment is reloaded
     * @return `this`
     */
    fun whenReloaded(listener: Consumer<Environment>): Builder

    /**
     * Builds an [Environment] and registers it.
     *
     * @param plugin The owner for this environment
     */
    fun register(plugin: Any)
  }

  companion object {
    fun <T> getBinding(name: String, injector: Injector): Binding<T> {
      val hash = name.hashCode().toLong() * injector.hashCode().toLong()
      val binding = arrayOf(Anvil.bindingsCache[hash])
      if (binding[0] != null) {
        return binding[0] as Binding<T>
      }
      injector.bindings.forEach { (k: Key<*>, v: Binding<*>) ->
        if (k.typeLiteral.type.typeName.contains(name)) {
          binding[0] = v
        }
      }
      val result = Preconditions.checkNotNull(
        binding[0] as Binding<T>,
        "Could not find binding for service: $name in injector $injector"
      )
      Anvil.bindingsCache[hash] = result
      return result
    }

    @kotlin.jvm.JvmStatic
    fun <T> getInstance(name: String, injector: Injector): T = getProvider<T>(name, injector).get()
    fun <T> getKey(name: String, injector: Injector): Key<T> = getBinding<T>(name, injector).key
    fun <T> getProvider(name: String, injector: Injector): Provider<T> = getBinding<T>(name, injector).provider
  }
}
