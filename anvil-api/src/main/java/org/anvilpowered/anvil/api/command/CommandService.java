/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.api.command;

import java.util.List;
import java.util.function.Predicate;

public interface CommandService<TCommand, TCommandExecutor, TCommandSource> {

    void registerCommand(List<String> aliases, TCommand command, CommandNode<TCommand> node);

    TCommandExecutor generateRootCommand(
        String helpCommandName,
        Predicate<TCommandSource> extended
    );

    default TCommandExecutor generateRootCommand(String helpCommandName) {
        return generateRootCommand(helpCommandName, e -> true);
    }

    TCommandExecutor generateVersionCommand(
        String helpCommandName,
        Predicate<TCommandSource> extended
    );

    default TCommandExecutor generateVersionCommand(String helpCommandName) {
        return generateVersionCommand(helpCommandName, e -> true);
    }

    /**
     * Generates a help command for the provided {@link CommandNode}.
     */
    TCommandExecutor generateHelpCommand(CommandNode<TCommand> node);

    TCommandExecutor generateReloadCommand();
}
