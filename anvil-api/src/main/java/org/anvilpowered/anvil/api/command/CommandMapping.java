/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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

import org.anvilpowered.anvil.api.misc.Named;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface CommandMapping<C> extends Named {

  /**
   * @return The primary alias
   */
  @Override
  String getName();

  List<String> getOtherAliases();

  C getCommand();

  Optional<? extends CommandMapping<C>> getParentCommand();

  /**
   * @return A list of subcommands, empty if there are none (i.e. this command is terminal)
   */
  List<? extends CommandMapping<C>> getSubCommands();
}
