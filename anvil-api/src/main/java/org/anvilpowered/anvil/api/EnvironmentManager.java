/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api;

import org.anvilpowered.anvil.api.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface EnvironmentManager {

    Environment getCoreEnvironment();

    Environment getEnvironmentUnsafe(String name);

    Map<String, Environment> getEnvironments();

    Stream<Environment> getEnvironmentsAsStream(Pattern pattern);

    List<Environment> getEnvironments(Pattern pattern);

    Optional<Environment> getEnvironment(Pattern pattern);

    Optional<Environment> getEnvironment(String name);

    Environment getEnvironment(Plugin<?> plugin);

    Set<Environment> getEnvironments(Plugin<?> plugin);
}
