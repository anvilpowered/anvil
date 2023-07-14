package org.anvilpowered.anvil.domain.command

import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.domain.RepositoryScope
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSuspending
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get

context(RepositoryScope)
fun ArgumentBuilder.Companion.gameUser(
    name: String,
    command: suspend (context: CommandContext<CommandSource>, gameUser: GameUser) -> Int,
): RequiredArgumentBuilder<CommandSource, String> =
    required<CommandSource, String>(name, StringArgumentType.SingleWord)
        .suggests { _, builder ->
            gameUserRepository.getAllUserNames(startWith = builder.input).forEach { name -> builder.suggest(name) }
            builder.build()
        }
        .executesSuspending { context ->
            val gameUserName = context.get<String>(name)
            gameUserRepository.findByUsername(gameUserName)?.let { gameUser ->
                command(context, gameUser)
            } ?: run {
                context.source.audience.sendMessage(
                    Component.text()
                        .append(Component.text("GameUser with name ", NamedTextColor.RED))
                        .append(Component.text(gameUserName, NamedTextColor.GOLD))
                        .append(Component.text(" not found!", NamedTextColor.RED)),
                )
                0
            }
        }
