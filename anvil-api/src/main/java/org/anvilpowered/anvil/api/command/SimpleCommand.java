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

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public interface SimpleCommand<TString, TCommandSource> {

  // TODO: Put all arguments into an interface
  void execute(TCommandSource source, String alias, String[] context);

  default List<String> suggest(TCommandSource source, String alias, String[] context) {
    return ImmutableList.of();
  }

  default boolean canExecute(TCommandSource source) {
    return true;
  }

  default Optional<TString> getShortDescription(TCommandSource source) {
    return Optional.empty();
  }

  default Optional<TString> getLongDescription(TCommandSource source) {
    return Optional.empty();
  }

  default Optional<TString> getUsage(TCommandSource source) {
    return Optional.empty();
  }
}
