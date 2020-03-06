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

import org.anvilpowered.anvil.api.misc.Named;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface CommandNode<TCommand> extends Named {

    /**
     * @return The root command of this node
     */
    TCommand getCommand();

    Map<List<String>, TCommand> getCommands();

    Map<List<String>, Function<Object, String>> getDescriptions();

    Map<List<String>, Predicate<Object>> getPermissions();

    Map<List<String>, Function<Object, String>> getUsages();

    /**
     * @return An array containing (in order) the names
     * of parent nodes. Empty if this is the root node.
     */
    default String[] getPath() {
        return new String[0];
    }
}
