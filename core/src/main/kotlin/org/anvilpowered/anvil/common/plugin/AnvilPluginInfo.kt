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
package org.anvilpowered.anvil.common.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.plugin.PluginInfo

class AnvilPluginInfo : PluginInfo {
    override var prefix: Component = Component.text()
        .append(Component.text("[", NamedTextColor.BLUE))
        .append(Component.text(Companion.name, NamedTextColor.AQUA))
        .append(Component.text("] ", NamedTextColor.BLUE))
        .build()
    override val id: String
        get() = Companion.id
    override val name: String
        get() = Companion.name
    override val version: String
        get() = Companion.version
    override val description: String
        get() = Companion.description
    override val url: String
        get() = Companion.url
    override val authors: Array<String>
        get() = Companion.authors
    override val organizationName: String
        get() = Companion.organizationName
    override val buildDate: String
        get() = Companion.buildDate

    companion object {
        const val id = "anvil"
        const val name = "Anvil"
        const val version = "\$modVersion"
        const val description = "A cross-platform Minecraft plugin framework"
        const val url = "https://github.com/AnvilPowered/Anvil"
        const val organizationName = "AnvilPowered"
        val authors = arrayOf(organizationName)
        const val buildDate = "\$buildDate"
    }
}