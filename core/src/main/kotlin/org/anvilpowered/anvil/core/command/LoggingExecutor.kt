/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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

package org.anvilpowered.anvil.core.command

import org.apache.logging.log4j.Logger

fun CommandExecutor.withLogging(logger: Logger, prefix: String = "command"): CommandExecutor = object : CommandExecutor {
    private fun log(success: Boolean, prefix: String, command: String) {
        if (success) {
            logger.info("$prefix: $command")
        } else {
            logger.error("Failed to execute $prefix: $command")
        }
    }

    override suspend fun execute(source: CommandSource, command: String): Boolean {
        val success = this@withLogging.execute(source, command)
        log(success, prefix, command)
        return success
    }

    override suspend fun executeAsConsole(command: String): Boolean {
        val success = this@withLogging.executeAsConsole(command)
        log(success, "console via $prefix", command)
        return success
    }
}
