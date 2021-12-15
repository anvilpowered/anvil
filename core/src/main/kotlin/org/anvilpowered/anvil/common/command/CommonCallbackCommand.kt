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
import org.anvilpowered.anvil.api.command.SimpleCommand
import java.util.UUID
import java.util.function.Consumer

class CommonCallbackCommand<TCommandSource> : SimpleCommand<TCommandSource> {

  @Inject
  private lateinit var textService: TextService<TCommandSource>

  private val missingId: Component by lazy {
    textService.builder()
      .appendPrefix()
      .red().append("Missing callback id")
      .build()
  }

  private val invalidId: Component by lazy {
    textService.builder()
      .appendPrefix()
      .red().append("Callback id must be a valid UUID")
      .build()
  }

  fun addCallback(uuid: UUID, callback: Consumer<TCommandSource>) {
    CallbackCommandData<TCommandSource>().callbacks[uuid] = callback::accept
  }

  override fun execute(source: TCommandSource, context: Array<String>) {
    if (context.isEmpty()) {
      textService.send(missingId, source)
      return
    }
    val uuid: UUID = try {
      UUID.fromString(context[0])
    } catch (e: IllegalArgumentException) {
      textService.send(invalidId, source)
      return
    }
    val callback = CallbackCommandData<TCommandSource>().callbacks[uuid]
    if (callback == null) {
      textService.builder()
        .appendPrefix()
        .red().append("Callback id ")
        .gold().append(uuid)
        .red().append(" does not match any callbacks")
        .sendTo(source)
      return
    }
    callback(source)
  }
}
