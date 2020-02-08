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

package org.anvilpowered.anvil.api.plugin;

import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;

import java.util.Set;
import java.util.function.Consumer;

public interface Plugin<TPluginContainer> {

    String getName();

    TPluginContainer getPluginContainer();

    /**
     * <p>
     * Note: while not explicitly necessary, it is generally advisable
     * to override this method by getting the {@link Environment}
     * from {@link Environment.Builder#whenReady(Consumer)} and setting
     * it in a field in your implementing class
     * </p>
     *
     * @return The primary {@link Environment} for this {@link Plugin}
     */
    default Environment getPrimaryEnvironment() {
        return Anvil.getEnvironment(this);
    }

    /**
     * @return A {@link Set} of all {@link Environment environments} registered to this {@link Plugin}
     */
    default Set<Environment> getAllEnvironments() {
        return Anvil.getEnvironments(this);
    }
}
