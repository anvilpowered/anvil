/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.core.config

import org.anvilpowered.anvil.core.config.ConfigurateRegistry.Factory.DiscoveryClosure
import org.apache.logging.log4j.Logger
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.extension
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.notExists

class ConfigurateRegistry(
  private val rootNode: ConfigurationNode,
  private val delegate: Registry? = null,
) : Registry {
  override fun <T : Any> getDefault(key: Key<T>): T = delegate?.getDefault(key) ?: key.fallback
  override fun <E : Any> getDefault(key: ListKey<E>, index: Int): E {
    return delegate?.getDefault(key, index)
      ?: key.fallback.getOrNull(index)
      ?: throw NoSuchElementException("No default value for key ${key.name} at index $index")
  }

  override fun <K : Any, V : Any> getDefault(key: MapKey<K, V>, mapKey: K): V {
    return delegate?.getDefault(key, mapKey)
      ?: key.fallback[mapKey]
      ?: throw NoSuchElementException("No default value for key ${key.name} at key $mapKey")
  }

  override fun <T : Any> getStrict(key: SimpleKey<T>): T? = rootNode.node(key.configNodePath)[key.type]
  override fun <E : Any> getStrict(key: ListKey<E>): List<E>? = rootNode.node(key.configNodePath)[key.type]
  override fun <E : Any> getStrict(key: ListKey<E>, index: Int): E? = getStrict(key)?.let { return it[index] }
  override fun <K : Any, V : Any> getStrict(key: MapKey<K, V>): Map<K, V>? = rootNode.node(key.configNodePath)[key.type]
  override fun <K : Any, V : Any> getStrict(key: MapKey<K, V>, mapKey: K): V? = getStrict(key)?.let { return it[mapKey] }

  companion object Factory {

    data class DiscoverResult internal constructor(
      val registry: Registry,
      val path: Path,
      val type: ConfigurateFileType<*>,
    )

    fun discover(
      basePath: Path,
      logger: Logger,
      serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
      delegate: Registry? = null,
    ): DiscoverResult? {
      if (basePath.notExists()) {
        basePath.createDirectory()
      }

      val configFiles = basePath.listDirectoryEntries()
        .map { it to ConfigurateFileType.fromName(it.extension) }
        .mapNotNull { (path, type) -> type?.let { path to it } }
        .toList()

      if (configFiles.isEmpty()) {
        return null
      } else if (configFiles.size >= 2) {
        throw IllegalStateException(
          "Detected multiple configuration files for plugin ${basePath.fileName}: ${configFiles.map { it.first }}. " +
            "Please make sure there is only one configuration file per plugin",
        )
      }

      val (path, type) = configFiles.single()
      return DiscoverResult(ConfigurateRegistry(type.createBuilder(serializers).path(path).build().load(), delegate), path, type)
    }

    fun createDiscoveryClosure(
      basePath: Path,
      logger: Logger,
      serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
      delegate: Registry? = null,
    ) = DiscoveryClosure { discover(basePath, logger, serializers, delegate) }

    fun interface DiscoveryClosure {
      fun discover(): DiscoverResult?
    }
  }
}

val Key<*>.configNodePath: List<String>
  get() = name.split('_').map { it.lowercase() }
