package org.anvilpowered.anvil.plugin

import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.plugin.command.AnvilCommand
import org.anvilpowered.kbrig.tree.LiteralCommandNode

context(AnvilApi)
abstract class AnvilPlugin {

    fun registerCommands(registrationCallback: (LiteralCommandNode<CommandSource>) -> Unit) {
        logger.info("Registering commands...")
        registrationCallback(AnvilCommand.create())
        logger.info("Registered commands!")
    }
}
