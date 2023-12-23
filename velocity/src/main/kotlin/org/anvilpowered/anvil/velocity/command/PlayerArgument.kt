/*
 *   Catalyst - AnvilPowered.org
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

package org.anvilpowered.anvil.velocity.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSuspending
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get
import kotlin.jvm.optionals.getOrNull

fun ArgumentBuilder.Companion.requirePlayer(
    proxyServer: ProxyServer,
    argumentName: String = "player",
    command: suspend (context: CommandContext<CommandSource>, player: Player) -> Int,
): RequiredArgumentBuilder<CommandSource, String> =
    required<CommandSource, String>(argumentName, StringArgumentType.SingleWord)
        .suggests { _, builder ->
            proxyServer.allPlayers.forEach { player -> builder.suggest(player.username) }
            builder.build()
        }.executesSuspending { context ->
            context.requirePlayer(proxyServer, argumentName) { command(context, it) }
        }

suspend fun CommandContext<CommandSource>.requirePlayer(
    proxyServer: ProxyServer,
    argumentName: String = "player",
    command: suspend (player: Player) -> Int,
): Int {
    val playerName = get<String>(argumentName)
    return proxyServer.getPlayer(playerName).getOrNull()?.let { velocityPlayer ->
        command(velocityPlayer)
    } ?: run {
        source.sendMessage(
            Component.text()
                .append(Component.text("Player with name ", NamedTextColor.RED))
                .append(Component.text(playerName, NamedTextColor.GOLD))
                .append(Component.text(" not found!", NamedTextColor.RED)),
        )
        0
    }
}
