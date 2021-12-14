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
package org.anvilpowered.anvil.api.datastore

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

/**
 *
 *
 * A module consists of a [Manager] and a (single) [DBComponent]
 * for every data storage implementation.
 *
 *
 *
 * The [Manager] of a module is its metaphorical gateway. Interactions with
 * other modules should (almost always) be done through the [Manager].
 * There are, however, some cases where direct access to a component is required.
 * One such case is inter-[Repository] access that requires compile time
 * type safety. Because the [DBComponent.getTKeyClass] type is not known
 * to the manager, code that interacts with `TKey` must be placed in a
 * [DBComponent].
 *
 *
 *
 * One of the primary functions of a [Manager] is to provide the correct
 * [DBComponent] implementation via [.getPrimaryComponent].
 *
 *
 *
 * Implementations of [Manager] should consist of methods similar to the
 * following:
 *
 *
 *  * `[CompletableFuture]<[Component]> create(UUID userUUID);`
 *  * `[CompletableFuture]<[Component]> invite(UUID userUUID, UUID targetUserUUID);`
 *  * `[CompletableFuture]<[Component]> kick(UUID userUUID, UUID targetUserUUID);`
 *  * `[CompletableFuture]<List<[Component]>> list(String query);`
 *
 *
 *
 * [Component] is the base return type for the methods in a [Manager].
 * To build these results use [TextService.Builder] or the static methods in [Component].
 *
 *
 *
 * All methods (with some exceptions) in [Manager] should return a form of [Component]
 * to be displayed directly to the end user. Normally, the return type, [Component], is wrapped in
 * a [java.util.concurrent.CompletableFuture] in order to keep the main game thread
 * free from IO. It is sometimes necessary to further wrap the [Component] in a [java.util.List]
 * when the result is more than a single line. In this case, pagination can be used to display the result
 * to the end user.
 *
 *
 *
 * The following is an example of a typical [Manager] and [Repository] combination:
 *
 * <pre>`
 * public interface FooManager
 * extends Manager<FooRepository<?, ?>> {...}
 *
 * public interface FooRepository<TKey, TDataStore>
 * extends Repository<TKey, Foo<TKey>, TDataStore> {...}
`</pre> *
 *
 * @param <C> Base [DBComponent] type for this manager.
 * Must be implemented by all components in this module
 * @see Repository
 *
 * @see DBComponent
 *
 * @see TextService
</C> */
interface Manager<C : DBComponent<*, *>?> {
  /**
   * Provides the current [DBComponent] as defined by [Keys.DATA_STORE_NAME] in the current [Registry].
   *
   *
   * The current [DBComponent] implementation is defined as the implementation provided by Guice that meets the following
   * criteria:
   *
   * <br></br>
   *
   *
   * The value for [Keys.DATA_STORE_NAME] found by the the current [Registry] must match (ignored case) a registered
   * datastore implementation. This can be one of the following predefined values:
   *
   *
   *  * `"mongodb"`
   *  * `"xodus"`
   *
   *
   *
   * or a custom value defined by your guice module.
   *
   * <br></br>
   *
   *
   * For example, 'mongodb' (or any capitalization thereof) will match
   * a [DBComponent] annotated with [Named]`(value = "mongodb")`
   *
   *
   * @return The current [DBComponent] implementation
   * @throws IllegalStateException If the config has not been loaded yet, or if no implementation was found
   * @see DBComponent
   */
  val primaryComponent: C
}
