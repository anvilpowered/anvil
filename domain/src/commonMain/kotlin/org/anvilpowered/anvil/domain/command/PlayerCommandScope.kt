package org.anvilpowered.anvil.domain.command

import org.anvilpowered.anvil.domain.user.CommandSource
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.context.CommandContext

interface PlayerCommandScope {

    fun ArgumentBuilder.Companion.player(
        name: String = "player",
        command: (context: CommandContext<CommandSource>, player: Player) -> Int,
    ): RequiredArgumentBuilder<CommandSource, String>
}
