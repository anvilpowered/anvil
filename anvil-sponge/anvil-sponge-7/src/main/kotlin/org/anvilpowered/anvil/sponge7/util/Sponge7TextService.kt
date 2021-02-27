/*
 * Anvil - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package org.anvilpowered.anvil.sponge7.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.anvilpowered.anvil.common.util.CommonTextService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

open class Sponge7TextService : CommonTextService<CommandSource>() {

  override fun send(text: Component, receiver: CommandSource) {
    receiver.sendMessage(
      Text.of(TextSerializers.FORMATTING_CODE.deserialize(LegacyComponentSerializer.legacyAmpersand().serialize(text)))
    )
  }

  override fun getConsole(): CommandSource = Sponge.getServer().console
}
