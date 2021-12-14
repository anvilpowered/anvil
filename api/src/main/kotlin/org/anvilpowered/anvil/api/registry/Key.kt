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
import com.google.common.reflect.TypeToken
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
import org.anvilpowered.anvil.api.misc.Named
import java.util.function.Function

abstract class Key<T> internal constructor(
  val type: TypeToken<T>,
  override val name: String,
  fallbackValue: T?,
  userImmutable: Boolean,
  sensitive: Boolean,
  description: String?,
  parser: Function<String, T>?,
  toStringer: Function<T, String>?
) : Named, Comparable<Key<T>> {
  val fallbackValue: T
  val isUserImmutable: Boolean
  val isSensitive: Boolean
  val description: String?
  private val parser: Function<String, T>? = null
  private val toStringer: Function<T?, String>?

  init {
    this.fallbackValue = fallbackValue
    isUserImmutable = userImmutable
    isSensitive = sensitive
    this.description = description
    if (parser == null) {
      this.parser = extractParser(fallbackValue)
    } else {
      this.parser = parser
    }
    this.toStringer = toStringer
  }

  interface Builder<T> {
    /**
     * Sets the name of the generated [Key]
     *
     * @param name The name to set
     * @return `this`
     */
    fun name(name: String): Builder<T>

    /**
     * Sets the fallback value of the generated [Key]
     *
     * @param fallbackValue The fallback value to set
     * @return `this`
     */
    fun fallback(fallbackValue: T?): Builder<T>

    /**
     * Indicates that the generated [Key] cannot be changed by the user.
     *
     * @return `this`
     */
    fun userImmutable(): Builder<T>

    /**
     * Indicates that the generated [Key] is sensitive (e.g. connection details) that should not
     * be accessible through regedit by default. Values of sensitive keys can only be viewed or modified
     * through registries that have [Keys.REGEDIT_ALLOW_SENSITIVE] enabled.
     *
     * @return `this`
     */
    fun sensitive(): Builder<T>

    /**
     * Sets the description of the generated [Key].
     *
     * @param description The description to set or `null` to remove it
     * @return `this`
     */
    fun description(description: String?): Builder<T>

    /**
     * Sets the parser of the generated [Key].
     *
     * @param parser The parser to set or `null` to remove it
     * @return `this`
     */
    fun parser(parser: Function<String, T>?): Builder<T>

    /**
     * Sets the toStringer of the generated [Key].
     *
     * @param toStringer The toStringer to set or `null` to remove it
     * @return `this`
     */
    fun toStringer(toStringer: Function<T, String>?): Builder<T>

    /**
     * Generates a [Key] based on this builder.
     *
     * @return The generated [Key]
     */
    fun build(): Key<T>
  }

  private fun extractParser(value: T?): Function<String, T>? {
    if (value is String) {
      return Function { s: String -> s as T }
    } else if (value is Boolean) {
      return Function { s: String? -> java.lang.Boolean.valueOf(s) as T }
    } else if (value is Double) {
      return Function { s: String? -> java.lang.Double.valueOf(s) as T }
    } else if (value is Float) {
      return Function { s: String? -> java.lang.Float.valueOf(s) as T }
    } else if (value is Long) {
      return Function { s: String? -> java.lang.Long.valueOf(s) as T }
    } else if (value is Int) {
      return Function { s: String? -> Integer.valueOf(s) as T }
    } else if (value is Short) {
      return Function { s: String -> s.toShort() as T }
    } else if (value is Byte) {
      return Function { s: String? -> java.lang.Byte.valueOf(s) as T }
    }
    return null
  }

  fun parse(value: String): T? {
    return parser?.apply(value)
  }

  fun toString(value: T?): String {
    return toStringer?.apply(value) ?: value.toString()
  }

  override fun compareTo(o: Key<T>): Int {
    return name.compareTo(o.name, ignoreCase = true)
  }

  override fun equals(o: Any?): Boolean {
    return o is Key<*> && name.equals(o.name, ignoreCase = true)
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }

  override fun toString(): String {
    return name
  }

  fun isSensitive(registry: Registry): Boolean {
    return isSensitive && !registry.get<Boolean?>(Keys.Companion.REGEDIT_ALLOW_SENSITIVE).orElse(false)!!
  }

  companion object {
    fun <T> builder(type: TypeToken<T>): Builder<T> {
      return KeyBuilder(type)
    }
  }
}
