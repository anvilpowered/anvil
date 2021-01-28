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
package org.anvilpowered.anvil.common.registry

import com.google.inject.Inject
import com.google.inject.Singleton
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.base.registry.BaseConfigurationService

@Singleton
open class CommonConfigurationService @Inject constructor(
    configLoader: ConfigurationLoader<CommentedConfigurationNode>,
) : BaseConfigurationService(configLoader) {
    init {
        withDataStoreCore()
        withDefault()
        withProxyMode()
        setName(Keys.REGEDIT_ALLOW_SENSITIVE, "server.regeditAllowSensitive")
        setName(Keys.TIME_ZONE, "server.timezone")
        setDescription(Keys.REGEDIT_ALLOW_SENSITIVE, """
Whether the regedit command should have access to sensitive settings such as connection details.
""")
        setDescription(Keys.TIME_ZONE, """
The server's timezone id. Use "auto" for the local system time, otherwise
please see https://nodatime.org/TimeZones (note that your system's available timezones may differ).
This option is useful if your server machine and community are based in different timezones.
""")
        val serializers = TypeSerializerCollection.defaults().newChild()
        serializers.register(Keys.TIME_ZONE.type, CommonZoneIdSerializer())
        val options = ConfigurationOptions.defaults()
        setOptions(options.withSerializers(serializers))
    }
}
