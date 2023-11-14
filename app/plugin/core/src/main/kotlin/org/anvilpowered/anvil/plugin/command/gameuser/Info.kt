package org.anvilpowered.anvil.plugin.command.gameuser

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.plugin.command.common.executesUsage
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.tree.LiteralCommandNode

private val children = mapOf(
    "help" to Component.text("Shows this help message"),
    "info" to Component.text("Shows information about a game user"),
)

context(AnvilApi)
fun GameUserCommand.createInfo(): LiteralCommandNode<CommandSource> =
    ArgumentBuilder.literal<CommandSource>("info")
        .executesUsage("anvil gameuser info <name|uuid>")
        .build()
