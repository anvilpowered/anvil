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

import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.time.Instant
import java.time.ZonedDateTime
import org.anvilpowered.anvil.api.util.TimeFormatService.FormatResult
import java.time.temporal.TemporalAccessor
import org.anvilpowered.anvil.api.model.ObjectWithId
import kotlin.Throws
import java.io.IOException
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import org.anvilpowered.anvil.api.model.Mappable
import java.lang.ClassNotFoundException
import java.io.ObjectInputStream
import java.lang.ClassCastException
import java.lang.UnsupportedOperationException
import jetbrains.exodus.util.ByteArraySizedInputStream
import java.lang.SafeVarargs
import com.google.common.collect.ImmutableList
import java.util.stream.Collectors
import org.anvilpowered.anvil.api.entity.RestrictionCriteria
import com.google.common.base.MoreObjects
import com.google.common.base.Preconditions
import org.anvilpowered.anvil.api.registry.Keys.KeyRegistrationEnd
import java.lang.AssertionError
import com.google.common.collect.HashBasedTable
import com.google.inject.Binding
import com.google.inject.Injector
import com.google.inject.Module
import org.anvilpowered.anvil.api.registry.TypeTokens
import java.time.ZoneId
import org.anvilpowered.anvil.api.registry.ZoneIdSerializer
import org.anvilpowered.anvil.api.registry.RegistryScoped
import java.util.function.BiFunction
import org.anvilpowered.anvil.api.registry.RegistryScope
import java.lang.Runnable
import org.anvilpowered.anvil.api.registry.Registry.ListenerRegistrationEnd
import org.anvilpowered.anvil.api.datastore.DBComponent
import org.anvilpowered.anvil.api.datastore.DataStoreContext
import java.net.URLEncoder
import java.io.UnsupportedEncodingException
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import java.nio.file.Paths
import java.lang.IllegalStateException
import org.anvilpowered.anvil.api.datastore.XodusEntity
import org.anvilpowered.anvil.api.datastore.XodusEmbedded
import java.lang.NoSuchMethodException
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.StoreTransaction
import org.anvilpowered.anvil.api.datastore.CacheService
import java.util.function.BiConsumer
import java.lang.RuntimeException
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import org.anvilpowered.anvil.base.plugin.BasePlugin
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.EnvironmentManager
import org.anvilpowered.anvil.api.coremember.CoreMemberManager
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository
import java.lang.InstantiationException
import java.lang.IllegalAccessException
import org.anvilpowered.anvil.api.datastore.Manager
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.api.datastore.MongoRepository
import org.anvilpowered.anvil.api.datastore.XodusRepository
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.util.TextService
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.api.registry.Registry
import java.util.HashMap

open class Anvil internal constructor(name: String?, rootInjector: Injector?, module: Module?) :
  BasePlugin(name, rootInjector, module) {
  companion object {
    val bindingsCache: MutableMap<Long, Binding<*>?> = HashMap()
    protected var serviceManager: ServiceManager? = null
    @kotlin.jvm.JvmField
    var environment: Environment? = null
    private const val NOT_LOADED = "Anvil has not been loaded yet!"
    @kotlin.jvm.JvmStatic
    val environmentBuilder: Environment.Builder
      get() = getServiceManager()!!.provide(Environment.Builder::class.java)
    @kotlin.jvm.JvmStatic
    val environmentManager: EnvironmentManager
      get() = getServiceManager()!!.provide(EnvironmentManager::class.java)

    fun getEnvironment(): Environment? {
      return Preconditions.checkNotNull(environment, NOT_LOADED)
    }

    val platform: Platform
      get() = getEnvironment().getInjector().getInstance(Platform::class.java)
    @kotlin.jvm.JvmStatic
    val registry: Registry
      get() = getEnvironment().getInjector().getInstance(Registry::class.java)
    val coreMemberManager: CoreMemberManager
      get() = getEnvironment().getInjector().getInstance(CoreMemberManager::class.java)
    @kotlin.jvm.JvmStatic
    val coreMemberRepository: CoreMemberRepository<*, *>?
      get() = coreMemberManager.primaryComponent

    @kotlin.jvm.JvmStatic
    fun getServiceManager(): ServiceManager? {
      return if (serviceManager != null) {
        serviceManager
      } else try {
        Class.forName("org.anvilpowered.anvil.api.ServiceManagerImpl").newInstance() as ServiceManager?. also {
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
  }
}
