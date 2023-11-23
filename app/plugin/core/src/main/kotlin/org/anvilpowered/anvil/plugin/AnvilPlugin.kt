package org.anvilpowered.anvil.plugin

import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.plugin.command.AnvilCommandFactory
import org.anvilpowered.kbrig.tree.LiteralCommandNode
import org.apache.logging.log4j.Logger

abstract class AnvilPlugin(
    private val logger: Logger,
    private val anvilCommandFactory: AnvilCommandFactory,
) {
    fun registerCommands(registrationCallback: (LiteralCommandNode<CommandSource>) -> Unit) {
        logger.info("Building command tree...")
        val command = anvilCommandFactory.create()
        logger.info("Registering commands...")
        registrationCallback(command)
        logger.info("Finished registering commands.")
    }
}
