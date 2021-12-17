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
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.TypeLiteral
import com.google.inject.name.Names
import com.google.inject.util.Modules
import org.anvilpowered.anvil.api.misc.toTypeLiteralNoInline
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.registry.RegistryScope
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.module.PlatformModule
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

class EnvironmentBuilderImpl internal constructor() : Environment.Builder {

    private lateinit var name: String
    private lateinit var rootInjector: Injector
    private lateinit var plugin: Any
    private lateinit var loggerSupplier: Supplier<*>
    private val withRootCommand = false
    private val modules: MutableCollection<Module>
    private val earlyServices: MutableMap<Key<*>, Consumer<*>>
    private val loadedListeners: MutableCollection<Consumer<Environment>>
    private val readyListeners: MutableCollection<Consumer<Environment>>
    private val reloadedListeners: MutableCollection<Consumer<Environment>>

    override fun addModules(vararg modules: Module): Environment.Builder {
        this.modules.addAll(listOf(*modules))
        return this
    }

    override fun addModules(modules: Iterable<Module>): Environment.Builder {
        modules.forEach(Consumer { e: Module -> this.modules.add(e) })
        return this
    }

    private fun <T> addEarlyServices(
        keys: Stream<T>,
        keyTransformer: Function<T, Key<*>>
    ): Environment.Builder {
        keys.forEach { key: T -> earlyServices[keyTransformer.apply(key)] = Consumer { _: Any? -> } }
        return this
    }

    override fun addEarlyServices(vararg keys: Key<*>): Environment.Builder {
        return addEarlyServices(Stream.of(*keys), Function.identity())
    }

    override fun addEarlyServices(keys: Iterable<Key<*>>): Environment.Builder {
        return addEarlyServices(StreamSupport.stream(keys.spliterator(), false),
            Function.identity())
    }

    override fun addEarlyServices(vararg classes: Class<*>): Environment.Builder {
        return addEarlyServices(Stream.of(*classes)) { type: Class<*>? -> Key.get(type) }
    }

    override fun addEarlyServices(vararg typeLiterals: TypeLiteral<*>): Environment.Builder {
        return addEarlyServices(Stream.of(*typeLiterals)) { typeLiteral: TypeLiteral<*>? -> Key.get(typeLiteral) }
    }

    override fun addEarlyServices(vararg typeTokens: TypeToken<*>): Environment.Builder {
        return addEarlyServices(Stream.of(*typeTokens)) { typeToken: TypeToken<*> -> Key.get(typeToken.toTypeLiteralNoInline()) }
    }

    override fun <T> addEarlyServices(key: Key<T>, initializer: Consumer<T>): Environment.Builder {
        earlyServices[key] = initializer
        return this
    }

    override fun <T> addEarlyServices(clazz: Class<T>, initializer: Consumer<T>): Environment.Builder {
        earlyServices[Key.get(clazz)] = initializer
        return this
    }

    override fun <T> addEarlyServices(typeLiteral: TypeLiteral<T>, initializer: Consumer<T>): Environment.Builder {
        earlyServices[Key.get(typeLiteral)] = initializer
        return this
    }

    override fun <T> addEarlyServices(typeToken: TypeToken<T>, initializer: Consumer<T>): Environment.Builder {
        earlyServices[Key.get(typeToken.toTypeLiteralNoInline())] = initializer
        return this
    }

    override fun setName(name: String): Environment.Builder {
        check(name.isNotEmpty()) { "Name may not be null or empty" }
        this.name = name
        return this
    }

    override fun setRootInjector(rootInjector: Injector): Environment.Builder {
        this.rootInjector = rootInjector
        return this
    }

    override fun setLoggerSupplier(logger: Supplier<*>): Environment.Builder {
        loggerSupplier = logger
        return this
    }

    override fun whenLoaded(listener: Consumer<Environment>): Environment.Builder {
        loadedListeners.add(listener)
        return this
    }

    override fun whenReady(listener: Consumer<Environment>): Environment.Builder {
        readyListeners.add(listener)
        return this
    }

    override fun whenReloaded(listener: Consumer<Environment>): Environment.Builder {
        reloadedListeners.add(listener)
        return this
    }

    override fun register(plugin: Any) {
        Preconditions.checkNotNull(name, "name")
        Preconditions.checkNotNull(plugin, "plugin")
        this.plugin = plugin
        builders.add(this)
    }

    companion object {
        private val builders: MutableCollection<EnvironmentBuilderImpl> = ArrayList()
        private var alreadyCompleted = false
        fun completeInitialization(platformModule: Module, fallbackModule: Module) {
            check(!alreadyCompleted) { "This method should only be called exactly once (in Anvil Common)" }
            alreadyCompleted = true
            val loadedListeners: MutableMap<String, Collection<Consumer<Environment>>> = HashMap()
            val readyListeners: MutableMap<String, Collection<Consumer<Environment>>> = HashMap()
            val reloadedListeners: MutableMap<String, Collection<Consumer<Environment>>> = HashMap()
            val environments = builders.stream().map { builder: EnvironmentBuilderImpl ->
                val name = builder.name
                loadedListeners[name] = builder.loadedListeners
                readyListeners[name] = builder.readyListeners
                reloadedListeners[name] = builder.reloadedListeners
                val modules: MutableList<Module> = ArrayList()
                modules.add(Modules.override(fallbackModule).with(builder.modules))
                EnvironmentImpl(
                    builder.rootInjector,
                    name,
                    builder.plugin,
                    builder.loggerSupplier,
                    builder.withRootCommand,
                    modules,
                    builder.earlyServices
                )
            }.collect(Collectors.toList())
            val commonModule: AbstractModule = object : AbstractModule() {
                override fun configure() {
                    for (environment in environments) {
                        bind(Environment::class.java)
                            .annotatedWith(Names.named(environment.name))
                            .toInstance(environment)
                    }
                }
            }
            // Naturally sort the environments
            environments.sort()
            for (environment in environments) {
                environment.addModule(platformModule)
                environment.addModule(commonModule)
                environment.addModule(object : AbstractModule() {
                    override fun configure() {
                        bind(ClassLoader::class.java).toInstance(environment.plugin
                            .javaClass.classLoader)
                        bind(Environment::class.java).toInstance(environment)
                        if (platformModule is PlatformModule) {
                            val platform = platformModule.platform
                            if (platform is PlatformImpl) {
                                platform.bindLoggerOptionally(environment, binder())
                            }
                        }
                    }
                })
                var injector: Injector? = environment.injector
                injector = if (injector != null) {
                    injector.createChildInjector(environment.getModules())
                } else {
                    Guice.createInjector(environment.getModules())
                }
                environment.injector = injector
                if ("anvil" == environment.name) {
                    Anvil.environment = environment
                    (Anvil.serviceManager as ServiceManagerImpl)
                        .setInjector(injector)
                }
                ServiceManagerImpl.environmentManager
                    .registerEnvironment(environment, environment.plugin)
                for ((key, value) in environment.earlyServices) {
                    (value as Consumer<Any>)
                        .accept(injector.getInstance(key))
                }
                val registry = injector.getInstance(Registry::class.java)
                for (listener in loadedListeners[environment.name]!!) {
                    registry.whenLoaded { listener.accept(environment) }.register()
                }
                registry.load(RegistryScope.DEEP)
                for (listener in reloadedListeners[environment.name]!!) {
                    registry.whenLoaded { listener.accept(environment) }.register()
                }
                for (listener in readyListeners[environment.name]!!) {
                    listener.accept(environment)
                }
            }
        }
    }

    init {
        modules = ArrayList()
        earlyServices = HashMap()
        loadedListeners = ArrayList()
        readyListeners = ArrayList()
        reloadedListeners = ArrayList()
    }
}