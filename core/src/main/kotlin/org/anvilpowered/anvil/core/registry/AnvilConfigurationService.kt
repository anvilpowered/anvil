/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.core.registry

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.anvil.api.registry.BaseConfigurationService
import org.anvilpowered.anvil.api.registry.ZoneIdSerializer
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.serialize.TypeSerializerCollection

@Singleton
class AnvilConfigurationService  @Inject constructor(
    configLoader: ConfigurationLoader<CommentedConfigurationNode>
) : BaseConfigurationService(configLoader) {

    init {
        withProxyMode()
        setName(AnvilKeys.REGEDIT_ALLOW_SENSITIVE, "server.regeditAllowSensitive")
        setName(AnvilKeys.TIME_ZONE, "server.timezone")
        setDescription(AnvilKeys.REGEDIT_ALLOW_SENSITIVE, """
Whether the regedit command should have access to sensitive settings such as connection details.
""")
        setDescription(AnvilKeys.TIME_ZONE, """
The server's timezone id. Use "auto" for the local system time, otherwise
please see https://nodatime.org/TimeZones (note that your system's available timezones may differ).
This option is useful if your server machine and community are based in different timezones.
""")
        val serializers = TypeSerializerCollection.defaults().childBuilder()
            .register(AnvilKeys.TIME_ZONE.typeToken, ZoneIdSerializer())
            .build()
        this.options = ConfigurationOptions.defaults().serializers(serializers)
    }
}
