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
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
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
import java.lang.IllegalArgumentException
import java.util.HashMap
import java.util.Optional
import java.util.function.Function

class Keys private constructor() {
  class KeyRegistrationEnd internal constructor(private val nameSpace: String) {
    private fun checkName(nameSpace: String, name: String) {
      require(!keys.contains(nameSpace, name)) {
        ("The provided key " + name + " conflicts with a"
          + " key of the same name in the " + nameSpace + " namespace.")
      }
    }

    /**
     * Registers the provided key.
     *
     * @param key The [Key] to register
     * @return `this`
     */
    fun register(key: Key<*>): KeyRegistrationEnd {
      val name: String = key.getName()
      checkName(nameSpace, name)
      keys.put(nameSpace, key.getName(), key)
      return this
    }
  }

  init {
    throw AssertionError("**boss music** No instance for you!")
  }

  companion object {
    private const val GLOBAL_NAMESPACE = "global"
    private val keys: Table<String, String, Key<*>> = HashBasedTable.create()
    private val localAndGlobalCache: MutableMap<String, Map<String, Key<*>>?> = HashMap()

    /**
     *
     *
     * Used to start the registration of keys in a namespace.
     *
     * <br></br>
     *
     *
     * Example usage:
     *
     * <pre>`
     * static {
     * Keys.startRegistration("ontime")
     * .register(RANKS)
     * .register(CHECK_PERMISSION)
     * .register(CHECK_EXTENDED_PERMISSION)
     * .register(EDIT_PERMISSION)
     * .register(IMPORT_PERMISSION);
     * }
    `</pre> *
     *
     * @param nameSpace The namespace to register the keys in. Usually the name of the plugin.
     * @return A [KeyRegistrationEnd] instance for registering keys
     */
    fun startRegistration(nameSpace: String): KeyRegistrationEnd {
      return KeyRegistrationEnd(nameSpace)
    }

    fun <T> resolveUnsafe(name: String?, nameSpace: String?): Key<T> {
      return Preconditions.checkNotNull(keys[nameSpace, name]) as Key<T>
    }

    fun <T> resolve(name: String?, nameSpace: String?): Optional<Key<T>> {
      return Optional.ofNullable(keys[nameSpace, name] as Key<T>)
    }

    fun <T> resolveUnsafe(name: String): Key<T> {
      return resolve<Any>(name).orElseThrow {
        IllegalArgumentException(
          "Could not resolve key $name")
      } as Key<T>
    }

    fun <T> resolve(name: String): Optional<Key<T>> {
      val candidate = keys[GLOBAL_NAMESPACE, name] as Key<T>?
      if (candidate != null) {
        return Optional.of(candidate)
      }
      val it: Iterator<Key<*>> = keys.column(name).values.iterator()
      return if (it.hasNext()) {
        Optional.of(it.next() as Key<T>)
      } else {
        Optional.empty()
      }
    }

    fun <T> resolveLocalAndGlobal(name: String?, nameSpace: String?): Optional<Key<T>> {
      val candidate = keys[nameSpace, name] as Key<T>?
      return if (candidate != null) {
        Optional.of(candidate)
      } else Optional.ofNullable(keys[GLOBAL_NAMESPACE, name] as Key<T>)
    }

    fun getAll(nameSpace: String): Map<String, Key<*>>? {
      var result = localAndGlobalCache[nameSpace]
      if (result != null) {
        return result
      }
      result = ImmutableMap.builder<String, Key<*>>()
        .putAll(keys.row(nameSpace))
        .putAll(keys.row(GLOBAL_NAMESPACE))
        .build()
      localAndGlobalCache[nameSpace] = result
      return result
    }

    @kotlin.jvm.JvmField
    val SERVER_NAME: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("SERVER_NAME")
      .fallback("server")
      .build()
    @kotlin.jvm.JvmField
    val TIME_ZONE: Key<ZoneId> = Key.Companion.builder<ZoneId>(TypeTokens.Companion.ZONE_ID)
      .name("TIME_ZONE")
      .fallback(ZoneId.systemDefault())
      .parser(Function<String, ZoneId> { input: String? -> ZoneIdSerializer.Companion.parse(input) })
      .toStringer(Function<ZoneId, String> { zoneId: ZoneId? -> ZoneIdSerializer.Companion.toString(zoneId) })
      .build()
    @kotlin.jvm.JvmField
    val PROXY_MODE: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("PROXY_MODE")
      .fallback(false)
      .build()
    val REGEDIT_ALLOW_SENSITIVE: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("REGEDIT_ALLOW_SENSITIVE")
      .fallback(false)
      .userImmutable()
      .build()
    val BASE_SCAN_PACKAGE: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("BASE_SCAN_PACKAGE")
      .fallback("org.anvilpowered.anvil.common.model")
      .userImmutable()
      .build()
    @kotlin.jvm.JvmField
    val CACHE_INVALIDATION_INTERVAL_SECONDS: Key<Int> = Key.Companion.builder<Int>(TypeTokens.Companion.INTEGER)
      .name("CACHE_INVALIDATION_INTERVAL_SECONDS")
      .fallback(30)
      .build()
    @kotlin.jvm.JvmField
    val CACHE_INVALIDATION_TIMOUT_SECONDS: Key<Int> = Key.Companion.builder<Int>(TypeTokens.Companion.INTEGER)
      .name("CACHE_INVALIDATION_TIMOUT_SECONDS")
      .fallback(300)
      .build()
    @kotlin.jvm.JvmField
    val USE_SHARED_ENVIRONMENT: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("USE_SHARED_ENVIRONMENT")
      .fallback(false)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val USE_SHARED_CREDENTIALS: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("USE_SHARED_CREDENTIALS")
      .fallback(false)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val DATA_DIRECTORY: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("DATA_DIRECTORY")
      .fallback("anvil")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val DATA_STORE_NAME: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("DATA_STORE_NAME")
      .fallback("xodus")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_CONNECTION_STRING: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("MONGODB_CONNECTION_STRING")
      .fallback("mongodb://admin:password@localhost:27017/anvil?authSource=admin")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_HOSTNAME: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("MONGODB_HOSTNAME")
      .fallback("localhost")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_PORT: Key<Int> = Key.Companion.builder<Int>(TypeTokens.Companion.INTEGER)
      .name("MONGODB_PORT")
      .fallback(27017)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_DBNAME: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("MONGODB_DBNAME")
      .fallback("anvil")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_USERNAME: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("MONGODB_USERNAME")
      .fallback("admin")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_PASSWORD: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("MONGODB_PASSWORD")
      .fallback("password")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_AUTH_DB: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("MONGODB_AUTH_DB")
      .fallback("admin")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_USE_AUTH: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("MONGODB_USE_AUTH")
      .fallback(false)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_USE_SRV: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("MONGODB_USE_SRV")
      .fallback(false)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val MONGODB_USE_CONNECTION_STRING: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("MONGODB_USE_CONNECTION_STRING")
      .fallback(false)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val REDIS_HOSTNAME: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("REDIS_HOSTNAME")
      .fallback("localhost")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val REDIS_PORT: Key<Int> = Key.Companion.builder<Int>(TypeTokens.Companion.INTEGER)
      .name("REDIS_PORT")
      .fallback(6379)
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val REDIS_PASSWORD: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("REDIS_PASSWORD")
      .fallback("password")
      .sensitive()
      .build()
    @kotlin.jvm.JvmField
    val REDIS_USE_AUTH: Key<Boolean?> = Key.Companion.builder<Boolean?>(TypeTokens.Companion.BOOLEAN)
      .name("REDIS_USE_AUTH")
      .fallback(false)
      .sensitive()
      .build()
    val PLUGINS_PERMISSION: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("PLUGINS_PERMISSION")
      .fallback("anvil.admin.plugins")
      .build()
    val REGEDIT_PERMISSION: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("REGEDIT_PERMISSION")
      .fallback("anvil.admin.regedit")
      .build()
    val RELOAD_PERMISSION: Key<String> = Key.Companion.builder<String>(TypeTokens.Companion.STRING)
      .name("RELOAD_PERMISSION")
      .fallback("anvil.admin.reload")
      .build()

    init {
      startRegistration(GLOBAL_NAMESPACE)
        .register(SERVER_NAME)
        .register(TIME_ZONE)
        .register(PROXY_MODE)
        .register(REGEDIT_ALLOW_SENSITIVE)
        .register(BASE_SCAN_PACKAGE)
        .register(CACHE_INVALIDATION_INTERVAL_SECONDS)
        .register(CACHE_INVALIDATION_TIMOUT_SECONDS)
        .register(USE_SHARED_ENVIRONMENT)
        .register(USE_SHARED_CREDENTIALS)
        .register(DATA_DIRECTORY)
        .register(DATA_STORE_NAME)
        .register(MONGODB_HOSTNAME)
        .register(MONGODB_PORT)
        .register(MONGODB_DBNAME)
        .register(MONGODB_USERNAME)
        .register(MONGODB_PASSWORD)
        .register(MONGODB_AUTH_DB)
        .register(MONGODB_USE_AUTH)
        .register(MONGODB_USE_SRV)
        .register(REDIS_HOSTNAME)
        .register(REDIS_PORT)
        .register(REDIS_PASSWORD)
        .register(REDIS_USE_AUTH)
      startRegistration("anvil")
        .register(PLUGINS_PERMISSION)
        .register(REGEDIT_PERMISSION)
        .register(RELOAD_PERMISSION)
    }
  }
}
