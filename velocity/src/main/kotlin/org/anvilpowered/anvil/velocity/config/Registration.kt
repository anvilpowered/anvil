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

package org.anvilpowered.anvil.velocity.config

import com.google.inject.Injector
import com.google.inject.Key
import com.velocitypowered.api.plugin.annotation.DataDirectory
import org.anvilpowered.anvil.core.config.Registry
import org.anvilpowered.anvil.core.config.configureDefaults
import org.apache.logging.log4j.Logger
import org.koin.core.module.Module
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.nio.file.Path

context(Module)
fun Registry.Companion.configureVelocityDefaults(
    injector: Injector,
    logger: Logger,
    serializers: TypeSerializerCollection = TypeSerializerCollection.defaults(),
) {
    configureDefaults(injector.getInstance(Key.get(Path::class.java, DataDirectory::class.java)), logger, serializers)
}
