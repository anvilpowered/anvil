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

import com.google.inject.Inject
import net.kyori.adventure.platform.spongeapi.SpongeAudiences
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.common.util.KyoriTextService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player

open class Sponge7TextService : KyoriTextService<CommandSource>() {

  @Inject
  private lateinit var adventure: SpongeAudiences

  override fun send(text: Component, receiver: CommandSource) = adventure.player(receiver as Player).sendMessage(text)
  override fun getConsole(): CommandSource = Sponge.getServer().console
}
