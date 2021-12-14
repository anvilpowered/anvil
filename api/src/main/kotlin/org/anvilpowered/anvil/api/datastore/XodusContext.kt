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
import com.google.inject.Inject
import com.google.inject.Singleton
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
import jetbrains.exodus.entitystore.Entity
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry

@Singleton
class XodusContext @Inject constructor(registry: Registry) : DataStoreContext<EntityId?, PersistentEntityStore>(registry) {
  override fun closeConnection(dataStore: PersistentEntityStore) {
    dataStore.close()
  }

  override fun loadDataStore(): PersistentEntityStore {

    /* === Initialize storage location === */
    val dbFilesLocation = Paths.get(registry.getOrDefault<String>(Keys.Companion.DATA_DIRECTORY) + "/data/xodus").toFile()
    if (!dbFilesLocation.exists()) {
      check(dbFilesLocation.mkdirs()) { "Unable to create xodus directory" }
    }

    /* === Find objects to map === */
    val entityClasses = calculateEntityClasses(registry.getOrDefault(Keys.Companion.BASE_SCAN_PACKAGE),
      XodusEntity::class.java,
      XodusEmbedded::class.java)

    /* === Create collections if not present === */for (entityClass in entityClasses!!) {
      if (Mappable::class.java.isAssignableFrom(entityClass)) {
        try {
          entityClass!!.getDeclaredMethod("writeTo", Entity::class.java)
          entityClass.getDeclaredMethod("readFrom", Entity::class.java)
        } catch (e: NoSuchMethodException) {
          throw IllegalStateException("Xodus entity class " + entityClass!!.name + " must implement Mappable#writeTo(T) and Mappable#readFrom(T)",
            e)
        }
      } else check(!entityClass!!.isAnnotationPresent(XodusEntity::class.java)) { "Xodus entity class " + entityClass.name + " must extend org.anvilpowered.anvil.model.data.dbo.Mappable" }
    }
    setTKeyClass(EntityId::class.java)
    return PersistentEntityStores.newInstance(dbFilesLocation.path)
  }
}
