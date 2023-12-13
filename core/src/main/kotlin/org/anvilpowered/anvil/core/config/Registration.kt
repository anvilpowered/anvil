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
import org.apache.logging.log4j.Logger
import org.koin.core.module.Module
import java.nio.file.Path

context(Module)
fun Registry.Companion.configureDefaults(basePath: Path, logger: Logger) {
    ConfigurateRegistryExporter.registerAll(basePath)
    val configurateRegistry = ConfigurateRegistry.discover(basePath)
    if (configurateRegistry == null) {
        logger.warn("No configuration file found, using environment variables only.")
    } else {
        logger.info("Using configuration file: ${configurateRegistry.path}")
    }
    single<Registry> { EnvironmentRegistry(get<PluginMeta>().name.uppercase(), configurateRegistry?.registry) }
}
