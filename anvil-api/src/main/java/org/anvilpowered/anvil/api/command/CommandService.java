/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.api.command;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface CommandService<TCommandExecutor, TCommandSource> {

    /**
     * Generates a command that runs a child command if its alias
     * matches the first argument of the generated command.
     *
     * <p>
     * If {@code childCommandFallback} is false
     * and the first argument does not match a child, a syntax error message will
     * be shown to the source. Otherwise, the root command executor will run.
     * </p>
     *
     * @param root                 The command to run if no child was specified.
     * @param children             A map of child commands and their aliases.
     * @param childCommandFallback whether to fall back to the root command if the first
     *                             argument did not match a child command alias
     * @throws UnsupportedOperationException on Sponge.
     *                                       Use {@code CommandSpec} instead.
     * @return A routing command
     */
    TCommandExecutor generateRoutingCommand(
        @Nullable TCommandExecutor root,
        Map<List<String>, TCommandExecutor> children,
        boolean childCommandFallback
    );

    TCommandExecutor generateRootCommand(
        String helpUsage,
        Predicate<TCommandSource> extended
    );

    default TCommandExecutor generateRootCommand(String helpUsage) {
        return generateRootCommand(helpUsage, e -> true);
    }

    TCommandExecutor generateVersionCommand(
        String helpUsage,
        Predicate<TCommandSource> extended
    );

    default TCommandExecutor generateVersionCommand(String helpUsage) {
        return generateVersionCommand(helpUsage, e -> true);
    }

    /**
     * Generates a help command for the provided {@link CommandNode}.
     * @param node The command node to return the HelpCommand for
     * @return A help command for the provided {@link CommandNode}
     */
    TCommandExecutor generateHelpCommand(CommandNode<TCommandSource> node);

    /**
     * @return A reload command
     */
    TCommandExecutor generateReloadCommand();
}
