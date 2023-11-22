package org.anvilpowered.anvil.paper.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.command.PlayerCommandScope
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.paper.user.toAnvilPlayer
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get
import org.bukkit.Bukkit

class PaperCustomCommand : PlayerCommandScope {

    override fun ArgumentBuilder.Companion.player(
        argumentName: String,
        command: (context: CommandContext<CommandSource>, player: Player) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String> =
        required<CommandSource, String>(argumentName, StringArgumentType.SingleWord)
            .suggests { _, builder ->
                Bukkit.getOnlinePlayers().forEach { player -> builder.suggest(player.name) }
                builder.build()
            }
            .executes { context ->
                val username = context.get<String>(argumentName)
                Bukkit.getPlayer(username)?.let { paperPlayer ->
                    command(context, paperPlayer.toAnvilPlayer())
                } ?: run {
                    context.source.audience.sendMessage(
                        Component.text()
                            .append(Component.text("Player with name ", NamedTextColor.RED))
                            .append(Component.text(username, NamedTextColor.GOLD))
                            .append(Component.text(" not found!", NamedTextColor.RED)),
                    )
                    0
                }
            }
}
