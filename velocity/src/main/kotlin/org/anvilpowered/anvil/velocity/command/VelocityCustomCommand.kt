package org.anvilpowered.anvil.velocity.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.command.PlayerCommandScope
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.velocity.AnvilVelocityApi
import org.anvilpowered.anvil.velocity.user.toAnvilPlayer
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get
import kotlin.jvm.optionals.getOrNull

context(AnvilVelocityApi)
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
                    command(context, velocityPlayer.toAnvilPlayer())
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
