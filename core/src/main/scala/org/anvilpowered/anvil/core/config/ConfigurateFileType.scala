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

import org.koin.core.module.Module
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader

import java.nio.file.Path

sealed trait ConfigurateFileType[
  B <: AbstractConfigurationLoader.Builder[B, AbstractConfigurationLoader[CommentedConfigurationNode]],
] {
  val name: String
  val fileExtension: String

  def createBuilder(serializers: TypeSerializerCollection): B

}

object ConfigurateFileType {
  object Hocon extends ConfigurateFileType[HoconConfigurationLoader.Builder] {
    override val name: String = "HOCON"
    override val fileExtension: String = "conf"

    override def toString(): String = fullName

    override def createBuilder(serializers: TypeSerializerCollection): HoconConfigurationLoader.Builder =
      HoconConfigurationLoader.builder().configure(serializers)
  }

  object Yaml extends ConfigurateFileType[YamlConfigurationLoader.Builder] {
    override val name: String = "YAML"
    override val fileExtension: String = "yaml"

    override def toString(): String = fullName

    override def createBuilder(serializers: TypeSerializerCollection): YamlConfigurationLoader.Builder =
      YamlConfigurationLoader.builder().configure(serializers).nodeStyle(NodeStyle.BLOCK)
  }

  def fromName(fileEnding: String): Option[ConfigurateFileType[*]] =
    fileEnding match {
      case Yaml.fileExtension => Yaml
      case Hocon.fileExtension => Hocon
      case _ => None
    }

  extension (fileType: ConfigurateFileType[?]) {
    def fullName: String = s"$name ($fileExtension)"
  }

  extension [B <: AbstractConfigurationLoader.Builder[B, *]](builder: B) {
    def configure(serializers: TypeSerializerCollection): B = {
      builder.defaultOptions { x =>
        x.serializers { b =>
          b.registerAll(serializers)
          // TODO: Object mapper factory?
        }
      }
    }
  }
}
