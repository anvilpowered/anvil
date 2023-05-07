package org.anvilpowered.anvil.plugin.command

import org.anvilpowered.anvil.domain.platform.PluginManager
import org.anvilpowered.anvil.domain.user.CommandSource
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

object AnvilCommand {
    context(PluginManager.Scope)
    fun create(): LiteralCommandNode<CommandSource> {
        return ArgumentBuilder.literal<CommandSource>("anvil")
            .then(createPlugins())
            .build()
    }
}
