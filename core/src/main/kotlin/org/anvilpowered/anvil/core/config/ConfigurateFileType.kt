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
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

sealed interface ConfigurateFileType<
  B : AbstractConfigurationLoader.Builder<B, out AbstractConfigurationLoader<CommentedConfigurationNode>>,
> {
  val name: String
  val fileExtension: String

  fun createBuilder(serializers: TypeSerializerCollection): B

  data object Hocon : ConfigurateFileType<HoconConfigurationLoader.Builder> {
    override val name: String = "HOCON"
    override val fileExtension: String = "conf"

    override fun toString(): String = fullName

    override fun createBuilder(serializers: TypeSerializerCollection): HoconConfigurationLoader.Builder =
      HoconConfigurationLoader.builder().configure(serializers)
  }

  data object Yaml : ConfigurateFileType<YamlConfigurationLoader.Builder> {
    override val name: String = "YAML"
    override val fileExtension: String = "yaml"

    override fun toString(): String = fullName

    override fun createBuilder(serializers: TypeSerializerCollection): YamlConfigurationLoader.Builder =
      YamlConfigurationLoader.builder().configure(serializers).nodeStyle(NodeStyle.BLOCK)
  }

  companion object {
    fun fromName(fileEnding: String): ConfigurateFileType<*>? =
      when (fileEnding) {
        Yaml.fileExtension -> Yaml
        Hocon.fileExtension -> Hocon
        else -> null
      }
  }
}

val ConfigurateFileType<*>.fullName: String
  get() = "$name ($fileExtension)"

context(Module)
fun ConfigurateFileType<*>.registerExporter(basePath: Path) {
  single {
    ConfigurateRegistryExporter(
      type = this@registerExporter,
      basePath = basePath,
      pluginMeta = get(),
      keyNamespace = get(),
    )
  }.withOptions { named(fileExtension) }
}

private fun <B : AbstractConfigurationLoader.Builder<B, *>> B.configure(serializers: TypeSerializerCollection): B =
  defaultOptions {
    it.serializers { builder ->
      builder.registerAll(serializers)
      builder.registerAnnotatedObjects(objectMapperFactory())
    }
  }
