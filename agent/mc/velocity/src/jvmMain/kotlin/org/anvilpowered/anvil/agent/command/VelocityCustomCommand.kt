package org.anvilpowered.anvil.agent.command

import kotlinx.coroutines.coroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.agent.ProxyServerScope
import org.anvilpowered.anvil.agent.user.toAnvil
import org.anvilpowered.anvil.datastore.GameUserScope
import org.anvilpowered.anvil.user.CommandSource
import org.anvilpowered.anvil.user.GameUser
import org.anvilpowered.anvil.user.Player
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSuspending
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get
import kotlin.jvm.optionals.getOrNull

class VelocityCustomCommand : CustomCommandScope {

    context(ProxyServerScope)
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
                    context.source.sendMessage(
                        Component.text()
                            .append(Component.text("Player with name ", NamedTextColor.RED))
                            .append(Component.text(playerName, NamedTextColor.GOLD))
                            .append(Component.text(" not found!", NamedTextColor.RED)),
                    )
                    0
                }
            }

    context(ProxyServerScope, GameUserScope)
    override fun ArgumentBuilder.Companion.gameUser(
        name: String,
        command: (context: CommandContext<CommandSource>, gameUser: GameUser) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String> =
        required<CommandSource, String>(name, StringArgumentType.SingleWord)
            .suggests { _, builder ->
                GameUser.getAllUserNames(startWith = builder.input).forEach { name -> builder.suggest(name) }
                builder.build()
            }
            .executesSuspending { context ->
                val gameUserName = context.get<String>(name)

                GameUser.findByUsername(gameUserName)?.let { gameUser ->
                    command(context, gameUser)
                } ?: run {

                }

                command(context, )
            }
}
