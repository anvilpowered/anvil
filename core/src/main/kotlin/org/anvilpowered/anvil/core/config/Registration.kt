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

import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.config.ConfigCommandFactory
import org.anvilpowered.anvil.core.platform.PluginMeta
import org.koin.core.module.Module
import org.spongepowered.configurate.serialize.TypeSerializerCollection

context(Module)
fun Registry.Companion.configureDefaults(
  anvil: AnvilApi,
  serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
) {
  ConfigurateRegistryExporter.registerAll(anvil.configDir)
  val configurateRegistryClosure = ConfigurateRegistry.createDiscoveryClosure(anvil.configDir, anvil.logger, serializers)
  val configurateRegistry = configurateRegistryClosure.discover()
  if (configurateRegistry == null) {
    anvil.logger.warn("No configuration file found, using environment variables only.")
  } else {
    anvil.logger.info("Using configuration file: ${configurateRegistry.path}")
  }
  single<ConfigurateRegistry.Factory.DiscoveryClosure> { configurateRegistryClosure }
  single<Registry> { EnvironmentRegistry(get<PluginMeta>().name.uppercase(), configurateRegistry?.registry) }
  single { ConfigCommandFactory(get(), get(), get(), getAll(), serializers) }
}
