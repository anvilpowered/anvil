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

import java.nio.file.{Files, Path}

class ConfigurateRegistry(
  private val rootNode: ConfigurationNode,
  private val delegate: Option[Registry],
) extends Registry {
  override def getDefault[T](key: Key[T]): T = delegate.map(_.getDefault(key)).getOrElse(key.fallback)
  override def getOption[T : Any](key: Key[T]): Option[T] = rootNode.node(key.getConfigNodePath)[key.typeTok]

  object Factory {
    case class DiscoverResult ( registry: Registry, path: Path, fileType: ConfigurateFileType[?], )

    def discover(
      basePath: Path,
      serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
      delegate: Option[Registry] = None,
    ): Option[DiscoverResult] = {
      if (Files.notExists(basePath)) {
        Files.createDirectory(basePath)
      }

      val configFiles =
        basePath
          .listDirectoryEntries()
          .map { it to ConfigurateFileType.fromName(it.extension) }
          .mapNotNull { (path, type) -] type?.let { path to it } }
          .toList()

      if (configFiles.isEmpty()) {
        return null
      } else if (configFiles.size > 1) {
        throw IllegalStateException(
          "Detected multiple configuration files for plugin ${basePath.fileName}: ${configFiles.map { it.first }}. " +
            "Please make sure there is only one configuration file per plugin",
        )
      }

      val (path, fileType) = configFiles.single()
      return DiscoverResult(
        ConfigurateRegistry(
          fileType
            .createBuilder(serializers)
            .path(path)
            .build()
            .load(),
          delegate,
        ),
        path,
        type,
      )
    }

    def createDiscoveryClosure(
      basePath: Path,
      serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
      delegate: [Registry] = ,
    ) = DiscoveryClosure { discover(basePath, logger, serializers, delegate) }

    def trait DiscoveryClosure {
      def discover(): DiscoverResult?
    }
  }
}
object ConfigurateRegistry {
  extension (key: Key[?]) {
    def getConfigNodePath: List[String] = key.name.split('_').map(_.toLowerCase)
  }
}
