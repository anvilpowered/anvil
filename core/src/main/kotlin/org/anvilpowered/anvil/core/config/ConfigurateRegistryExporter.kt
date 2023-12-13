/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
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

import org.anvilpowered.anvil.core.platform.PluginMeta
import org.koin.core.module.Module
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import java.nio.file.Path

class ConfigurateRegistryExporter(
    val type: ConfigurateFileType<*>,
    val basePath: Path,
    val pluginMeta: PluginMeta,
    val keyNamespace: KeyNamespace,
) {

    val configPath = basePath.resolve("${pluginMeta.name}.${type.fileExtension}")

    fun export(registry: Registry) = type.createBuilder()
        .path(configPath).build().save(keyNamespace.toConfigurationNode(registry))

    companion object {
        context(Module)
        fun registerAll(basePath: Path) {
            ConfigurateFileType.Hocon.registerExporter(basePath)
            ConfigurateFileType.Yaml.registerExporter(basePath)
        }
    }
}

private fun KeyNamespace.toConfigurationNode(registry: Registry): ConfigurationNode {
    val node = CommentedConfigurationNode.root()
    for (key in keys) {
        node.setFrom(key, registry)
    }
    return node
}

private fun <T : Any> CommentedConfigurationNode.setFrom(key: Key<T>, registry: Registry) {
    node(key.name).let { node ->
        node.set(key.type, registry[key])
        node.comment(key.description)
    }
}
