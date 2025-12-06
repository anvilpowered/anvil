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

package org.anvilpowered.anvil.velocity.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.CommandContextScopeDsl
import org.anvilpowered.kbrig.context.CommandExecutionScope
import org.anvilpowered.kbrig.context.executesScoped
import org.anvilpowered.kbrig.context.get
import org.anvilpowered.kbrig.context.yieldError
import org.anvilpowered.kbrig.suggestion.suggestsScoped
import kotlin.jvm.optionals.getOrNull

fun ArgumentBuilder.Companion.requirePlayerArgument(
  proxyServer: ProxyServer,
  argumentName: String = "player",
  command: suspend (context: CommandContext<CommandSource>, player: Player) -> Int,
): RequiredArgumentBuilder<CommandSource, String> =
  required<CommandSource, String>("player", StringArgumentType.SingleWord)
    .suggestPlayerArgument(proxyServer)
    .executesScoped { yield(command(context, extractPlayerArgument(proxyServer, argumentName))) }

fun ArgumentBuilder.Companion.requirePlayerArgumentScoped(
  proxyServer: ProxyServer,
  argumentName: String = "player",
  command: suspend CommandExecutionScope<CommandSource>.(player: Player) -> Unit,
): RequiredArgumentBuilder<CommandSource, String> =
  required<CommandSource, String>("player", StringArgumentType.SingleWord)
    .suggestPlayerArgument(proxyServer)
    .executesScoped { command(extractPlayerArgument(proxyServer, argumentName)) }

fun <S> RequiredArgumentBuilder<S, String>.suggestPlayerArgument(
  proxyServer: ProxyServer,
): RequiredArgumentBuilder<S, String> =
  suggestsScoped { proxyServer.allPlayers.suggestAllFiltered { it.username } }

@CommandContextScopeDsl
suspend fun CommandExecutionScope<CommandSource>.extractPlayerArgument(
  proxyServer: ProxyServer,
  argumentName: String = "player",
): Player {
  val playerName = context.get<String>(argumentName)
  val player = proxyServer.getPlayer(playerName).getOrNull()
  if (player == null) {
    context.source.sendMessage(
      Component.text()
        .append(Component.text("Player with name ", NamedTextColor.RED))
        .append(Component.text(playerName, NamedTextColor.GOLD))
        .append(Component.text(" not found!", NamedTextColor.RED))
        .build(),
    )
    yieldError()
  }
  return player
}

@CommandContextScopeDsl
suspend fun CommandExecutionScope<CommandSource>.extractPlayerSource(): Player {
  val player = extractPlayerSourceOrNull()
  if (player == null) {
    context.source.sendMessage(Component.text("You must be a player to use this command!", NamedTextColor.RED))
    yieldError()
  }
  return player
}

@CommandContextScopeDsl
fun CommandContext.Scope<CommandSource>.extractPlayerSourceOrNull(): Player? = context.source as? Player

@CommandContextScopeDsl
suspend fun CommandContext.Scope<CommandSource>.extractPlayerSourceOrAbort(): Player = extractPlayerSourceOrNull() ?: abort()
