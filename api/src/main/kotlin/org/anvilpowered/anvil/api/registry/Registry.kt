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
import org.anvilpowered.anvil.api.registry.Keys.KeyRegistrationEnd
import java.lang.AssertionError
import com.google.common.collect.HashBasedTable
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
import java.util.Optional
import java.util.function.Function

interface Registry {
  /**
   * Gets this registry's value for the provided [Key]
   * and throws an exception if it is not present.
   *
   * @param <T> The value type of the provided [Key]
   * @param key The [Key] to get the value for
   * @return This registry's value for the provided [Key]
   * @throws NoSuchElementException If this registry has no value defined
   * for the provided [Key]
  </T> */
  @RegistryScoped
  fun <T> getUnsafe(key: Key<T>?): T

  /**
   * Gets this registry's value for the provided [Key]
   * or [Optional.empty] if it is not present.
   *
   * @param <T> The value type of the provided [Key]
   * @param key The [Key] to get the value for
   * @return This registry's value for the provided [Key] or [Optional.empty]
  </T> */
  @RegistryScoped
  operator fun <T> get(key: Key<T>?): Optional<T>

  /**
   * Gets this registry's default value for the provided [Key]
   * or the fallback value if it is not present.
   *
   * @param <T> The value type of the provided [Key]
   * @param key The [Key] to get the default value for
   * @return This registry's default value for the provided [Key] or the fallback value
  </T> */
  @RegistryScoped
  fun <T> getDefault(key: Key<T>): T? {
    return key.fallbackValue
  }

  /**
   * Gets this registry's value for the provided [Key]
   * or the default value if it is not present.
   *
   * @param <T> The value type of the provided [Key]
   * @param key The [Key] to get the value or (if not present) the default value for
   * @return This registry's value for the provided [Key] or the default value
  </T> */
  @RegistryScoped
  fun <T> getOrDefault(key: Key<T>): T {
    return get(key).orElse(getDefault(key))
  }

  /**
   * Similar to [.getOrDefault], but performs additional (implementation specific)
   * checks that could potentially check other registries if certain requirements are met.
   * **Use [.getOrDefault] unless you are sure you need this.**
   *
   * @param <T> The value type of the provided [Key]
   * @param key The [Key] to get the value for
   * @return The value for the provided [Key] as defined by the additional checks
  </T> */
  @RegistryScoped
  fun <T> getExtraSafe(key: Key<T>): T {
    return getOrDefault(key)
  }

  /**
   * Sets this registry's value for the provided [Key]
   *
   * @param <T>   The value type of the provided [Key]
   * @param key   The [Key] to set the value for
   * @param value The value to set
  </T> */
  @RegistryScoped
  operator fun <T> set(key: Key<T>?, value: T)

  /**
   * Removes this registry's value for the provided [Key]
   *
   * @param <T> The value type of the provided [Key]
   * @param key The [Key] to set the value for
  </T> */
  @RegistryScoped
  fun <T> remove(key: Key<T>?)

  /**
   * Applies the provided transformation to this registry's
   * value for the provided [Key]
   *
   * @param <T>         The value type of the provided [Key]
   * @param key         The [Key] to transform the value for
   * @param transformer The transformation to apply
  </T> */
  @RegistryScoped
  fun <T> transform(key: Key<T>?, transformer: BiFunction<in Key<T>?, in T, out T>?)

  /**
   * Applies the provided transformation to this registry's
   * value for the provided [Key]
   *
   * @param <T>         The value type of the provided [Key]
   * @param key         The [Key] to transform the value for
   * @param transformer The transformation to apply
  </T> */
  @RegistryScoped
  fun <T> transform(key: Key<T>?, transformer: Function<in T, out T>?)

  /**
   * Adds the provided value to this registry's
   * [Collection] value for the provided [Key]
   *
   * @param <T>   The value type of the provided [Key]
   * @param key   The [Key] of the collection
   * to add the provided value to
   * @param value The value to add
  </T> */
  @RegistryScoped
  fun <T> addToCollection(key: Key<out MutableCollection<T>?>?, value: T)

  /**
   * Removes the provided value from this registry's
   * [Collection] value for the provided [Key]
   *
   * @param <T>   The value type of the provided [Key]
   * @param key   The [Key] of the collection
   * to add the provided value to
   * @param value The value to add
  </T> */
  @RegistryScoped
  fun <T> removeFromCollection(key: Key<out MutableCollection<T>?>?, value: T)

  /**
   * Puts the provided key and value pair to this registry's
   * [Map] value for the provided [Key]
   *
   * @param <K>      The key type of the map value for the provided key
   * @param <T>      The value type of the map value for the provided key
   * @param key      The [Key] of the map to add the
   * provided key and value pair to
   * @param mapKey   The map key to add
   * @param mapValue The map value to add
  </T></K> */
  @RegistryScoped
  fun <K, T> putInMap(key: Key<out MutableMap<K, T>?>?, mapKey: K, mapValue: T)

  /**
   * Removes the provided key from this registry's
   * [Map] value for the provided [Key]
   *
   * @param <K>    The key type of the map value for the provided key
   * @param <T>    The value type of the map value for the provided key
   * @param key    The [Key] of the map to remove the provided mapKey from
   * @param mapKey The map key to remove
  </T></K> */
  @RegistryScoped
  fun <K, T> removeFromMap(key: Key<out MutableMap<K, T?>?>?, mapKey: K)

  /**
   * Runs all [listeners][Runnable] that were
   * added before this call in the provided registryScope.
   *
   * @param registryScope The [RegistryScope] to load
   * @see Environment.reload
   * @see .whenLoaded
   */
  @RegistryScoped
  fun load(registryScope: RegistryScope?)

  /**
   * Runs all [listeners][Runnable] that were
   * added before this call in the [default scope][RegistryScope.DEFAULT].
   *
   * @see Environment.reload
   * @see .whenLoaded
   */
  @RegistryScoped
  fun load() {
    load(RegistryScope.DEFAULT)
  }

  /**
   * Adds a [Runnable] to be loaded on [.load].
   *
   *
   *
   * Listeners are grouped by order. Smaller orders run before larger ones.
   * The execution order within one order group is not guaranteed.
   *
   *
   *
   * Please note that [ListenerRegistrationEnd.register] must be invoked to
   * complete the registration.
   *
   *
   * @param listener Listener to add
   * @return A [ListenerRegistrationEnd] for specifying additional parameters and
   * completing the registration.
   * @see .load
   */
  fun whenLoaded(listener: Runnable?): ListenerRegistrationEnd
  interface ListenerRegistrationEnd {
    /**
     * Sets the order for the listener.
     * The default order is 0.
     *
     * @param order The order to run this listener in. Smaller is earlier.
     * @return `this`
     */
    fun order(order: Int): ListenerRegistrationEnd?

    /**
     * Sets the scope for the listener.
     * The default scope is [RegistryScope.DEFAULT].
     *
     * @param scope The scope to run this listener in.
     * @return `this`
     * @see RegistryScope
     *
     * @see RegistryScoped
     */
    fun scope(scope: RegistryScope?): ListenerRegistrationEnd?

    /**
     * Completes the listener registration.
     */
    fun register()
  }
}
