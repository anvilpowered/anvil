package org.anvilpowered.anvil.plugin.command.gameuser

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.api.user.requiresPermission
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Component
import org.anvilpowered.anvil.plugin.command.common.addHelp
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
    "help" to Component.text("Shows this help message"),
    "info" to Component.text("Shows information about a game user"),
)

object GameUserCommand {
    context(AnvilApi)
    fun create(): LiteralCommandNode<CommandSource> =
        ArgumentBuilder.literal<CommandSource>("gameuser")
            .addHelp("anvil gameuser", children)
            .requiresPermission("anvil.agent.gameuser")
            .build()
}