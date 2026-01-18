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

import cats.data.{EitherT, OptionT}
import cats.effect.kernel.Resource
import cats.effect.{Async, Concurrent, IO}
import cats.syntax.all.*
import fs2.io.file.{Files, Path}
import org.anvilpowered.anvil.core.config.ConfigurateRegistry.getConfigNodePath
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializerCollection

import scala.jdk.CollectionConverters.IterableHasAsScala

class ConfigurateRegistry(
    private val rootNode: ConfigurationNode,
    private val delegate: Option[Registry],
) extends Registry {
  override def getDefault[T](key: Key[T]): T = delegate.map(_.getDefault(key)).getOrElse(key.fallback)
  override def get[T](key: Key[T]): Option[T] = Option(rootNode.node(key.getConfigNodePath).get(key.typeToken))
}

object ConfigurateRegistry {
  case class DiscoverResult(registry: Registry, path: Path, fileType: ConfigurateFileType[?])

  def discover[F[_]](
      basePath: Path,
      serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
      delegate: Option[Registry] = None,
  )(using F: Async[F]): EitherT[F, String, DiscoverResult] =
    for {
      configFiles <- EitherT.liftF(
        Files.forAsync[F]
          .list(basePath)
          .mapFilter(path => ConfigurateFileType.fromName(path.extName).map(path -> _))
          .compile
          .toList,
      )
      (path, fileType) <- EitherT.cond(
        configFiles.size == 1,
        configFiles.head,
        s"Detected multiple configuration files for plugin ${basePath.fileName}: ${configFiles.map(_._1)}. " +
          "Please make sure there is only one configuration file per plugin",
      )
    } yield {
      val rootNode = fileType.createBuilder(serializers).path(path.toNioPath).build().load()
      val registry = ConfigurateRegistry(rootNode, delegate)
      DiscoverResult(registry, path, fileType)
    }

  extension (key: Key[?]) {
    def getConfigNodePath: List[String] = key.name.split('_').map(_.toLowerCase).toList
  }
}
