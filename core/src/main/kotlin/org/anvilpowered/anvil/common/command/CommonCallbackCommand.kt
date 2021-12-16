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
package org.anvilpowered.anvil.common.command

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.common.sendTo
import java.awt.event.ComponentEvent
import java.util.UUID
import java.util.function.Consumer

class CommonCallbackCommand<TCommandSource> : SimpleCommand<TCommandSource> {

  @Inject
  private lateinit var pluginInfo: PluginInfo

  private val missingId: Component =
      Component.text()
          .append(pluginInfo.prefix)
          .append(Component.text("Missing callback id").color(NamedTextColor.RED))
          .build()

  private val invalidId: Component =
      Component.text()
          .append(pluginInfo.prefix)
          .append(Component.text("Callback id must be a valid UUID").color(NamedTextColor.RED))
          .build()

  fun addCallback(uuid: UUID, callback: Consumer<TCommandSource>) {
    CallbackCommandData<TCommandSource>().callbacks[uuid] = callback::accept
  }

  override fun execute(source: TCommandSource, context: Array<String>) {
    if (context.isEmpty()) {
      missingId.sendTo(source)
      return
    }
    val uuid: UUID = try {
      UUID.fromString(context[0])
    } catch (e: IllegalArgumentException) {
      invalidId.sendTo(source)
      return
    }
    val callback = CallbackCommandData<TCommandSource>().callbacks[uuid]
    if (callback == null) {
      Component.text()
          .append(pluginInfo.prefix)
          .append(Component.text("Callback id ").color(NamedTextColor.RED))
          .append(Component.text(uuid.toString()).color(NamedTextColor.GOLD))
          .append(Component.text(" does not match any callbacks").color(NamedTextColor.RED))
          .build()
      //sendTo(source)
      return
    }
    callback(source)
  }
}
