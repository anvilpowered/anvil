package org.anvilpowered.anvil.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.ProxyServerScope
import org.anvilpowered.anvil.domain.command.PlayerCommandScope
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.user.toAnvil
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get
import kotlin.jvm.optionals.getOrNull

context(ProxyServerScope)
class VelocityCustomCommand : PlayerCommandScope {

    override fun ArgumentBuilder.Companion.player(
        name: String,
        command: (context: CommandContext<CommandSource>, player: Player) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String> =
        required<CommandSource, String>(name, StringArgumentType.SingleWord)
            .suggests { _, builder ->
                proxyServer.allPlayers.forEach { player -> builder.suggest(player.username) }
                builder.build()
            }
            .executes { context ->
                val playerName = context.get<String>(name)
                proxyServer.getPlayer(playerName).getOrNull()?.let { velocityPlayer ->
                    command(context, velocityPlayer.toAnvil())
                } ?: run {
                    context.source.audience.sendMessage(
                        Component.text()
                            .append(Component.text("Player with name ", NamedTextColor.RED))
                            .append(Component.text(playerName, NamedTextColor.GOLD))
                            .append(Component.text(" not found!", NamedTextColor.RED)),
                    )
                    0
                }
            }
}
