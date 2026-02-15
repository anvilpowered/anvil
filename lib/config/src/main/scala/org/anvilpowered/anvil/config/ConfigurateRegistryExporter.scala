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

package org.anvilpowered.anvil.config

import cats.data.EitherT
import cats.effect.{Async, IO}
import cats.syntax.all.*
import fs2.io.file.Path
import org.anvilpowered.anvil.config.ConfigurateRegistry.getConfigNodePath
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.{CommentedConfigurationNode, ConfigurateException}

class ConfigurateRegistryExporter[B <: AbstractConfigurationLoader.Builder[B, AbstractConfigurationLoader[CommentedConfigurationNode]]](
    val fileType: ConfigurateFileType[B],
    val basePath: Path,
    val pluginName: String,
    val keyNamespace: KeyNamespace,
) {
  val configPath: Path = basePath.resolve(s"$pluginName.${fileType.fileExtension}")

  def exportRegistry[F[_]: Async as F](registry: Registry, serializers: TypeSerializerCollection): EitherT[F, ConfigurateException, Unit] = EitherT(
    F.blocking {
      val loader = fileType.createBuilder(serializers).path(configPath.toNioPath).build()
      val root = loader.createNode()
      keyNamespace.keys.foreach { key =>
        val n = root.node(key.getConfigNodePath)
        n.set(key.typeToken, registry(key))
        key.description.foreach(n.comment)
      }
      loader.save(root)
    }.attemptNarrow[ConfigurateException],
  )
}
