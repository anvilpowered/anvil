/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
