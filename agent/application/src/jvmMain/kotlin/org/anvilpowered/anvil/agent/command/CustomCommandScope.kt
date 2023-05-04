package org.anvilpowered.anvil.agent.command

import org.anvilpowered.anvil.user.CommandSource
import org.anvilpowered.anvil.user.GameUser
import org.anvilpowered.anvil.user.Player
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext

interface CustomCommandScope {
    fun ArgumentBuilder.Companion.player(
        name: String = "player",
        command: (context: CommandContext<CommandSource>, player: Player) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String>

    fun ArgumentBuilder.Companion.gameUser(
        name: String = "gameUser",
        command: (context: CommandContext<CommandSource>, gameUser: GameUser) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String>
}
