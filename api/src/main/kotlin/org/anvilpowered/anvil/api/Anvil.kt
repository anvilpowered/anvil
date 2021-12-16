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

import com.google.inject.Binding
import com.google.inject.Injector
import com.google.inject.Module
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.base.plugin.BasePlugin

open class Anvil constructor(name: String, rootInjector: Injector, module: Module) :
    BasePlugin(name, rootInjector, module) {
    companion object {
        val bindingsCache: MutableMap<Long, Binding<*>> = HashMap()

        var serviceManager: ServiceManager? = null
            get() {
                return field
                    ?: try {
                        (Class.forName(
                            "org.anvilpowered.anvil.api.ServiceManagerImpl"
                        ).newInstance() as ServiceManager).also {
                            serviceManager = it
                        }
                    } catch (e: InstantiationException) {
                        throw IllegalStateException("Could not find ServiceManager implementation!", e)
                    } catch (e: IllegalAccessException) {
                        throw IllegalStateException("Could not find ServiceManager implementation!", e)
                    } catch (e: ClassNotFoundException) {
                        throw IllegalStateException("Could not find ServiceManager implementation!", e)
                    }
            }

        var environment: Environment? = null
            get() = field ?: throw java.lang.IllegalStateException(NOT_LOADED)

        private const val NOT_LOADED = "Anvil has not been loaded yet!"

        @kotlin.jvm.JvmStatic
        val environmentBuilder: Environment.Builder
            get() = serviceManager!!.provide(Environment.Builder::class.java)

        @kotlin.jvm.JvmStatic
        val environmentManager: EnvironmentManager
            get() = serviceManager!!.provide(EnvironmentManager::class.java)


        val platform: Platform
            get() = environment!!.injector.getInstance(Platform::class.java)

        @kotlin.jvm.JvmStatic
        val registry: Registry
            get() = environment!!.injector.getInstance(Registry::class.java)
        val coreMemberManager: CoreMemberManager
            get() = environment!!.injector.getInstance(CoreMemberManager::class.java)

        @kotlin.jvm.JvmStatic
        val coreMemberRepository = coreMemberManager.primaryComponent
    }
}
