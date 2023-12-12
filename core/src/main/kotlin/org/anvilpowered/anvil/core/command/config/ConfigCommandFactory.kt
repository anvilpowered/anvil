package org.anvilpowered.anvil.core.command.config

import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.config.KeyNamespace
import org.anvilpowered.anvil.core.config.Registry
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

class ConfigCommandFactory(
    val registry: Registry,
    val keyNamespace: KeyNamespace,
) {
    fun create(): LiteralCommandNode<CommandSource> {
        return ArgumentBuilder.literal<CommandSource>("config")
            .then(createGet())
            .build()
    }
}
