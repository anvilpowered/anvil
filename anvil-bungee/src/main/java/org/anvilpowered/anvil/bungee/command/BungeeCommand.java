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

package org.anvilpowered.anvil.bungee.command;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.BiConsumer;

public class BungeeCommand extends Command implements TabExecutor {

    private final BiConsumer<CommandSender, String[]> command;
    private final boolean withTab;

    public BungeeCommand(
        String name,
        @Nullable String permission,
        BiConsumer<CommandSender, String[]> command,
        String... aliases
    ) {
        super(name, permission, aliases);
        this.command = command;
        withTab = command instanceof TabExecutor;
    }

    @Override
    public void execute(CommandSender source, String[] context) {
        command.accept(source, context);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender source, String[] context) {
        if (withTab) {
            return ((TabExecutor) command).onTabComplete(source, context);
        }
        return ImmutableList.of();
    }
}
