package org.anvilpowered.anvil.plugin.command

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

object AnvilCommand {
    context(AnvilApi)
    fun create(): LiteralCommandNode<CommandSource> {
        return ArgumentBuilder.literal<CommandSource>("anvil")
            .then(createPlugins())
            .build()
    }
}
