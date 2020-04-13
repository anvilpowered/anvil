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

package org.anvilpowered.anvil.base.data.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.data.config.ConfigurationService;
import org.anvilpowered.anvil.api.data.key.Key;

import java.util.Optional;

/**
 * A registry that is backed by the configuration service
 */
@Singleton
public class BaseExtendedRegistry extends BaseRegistry {

    @Inject
    protected ConfigurationService configurationService;

    @Override
    public <T> Optional<T> get(Key<T> key) {
        Optional<T> result = super.get(key);
        return result.isPresent() ? result : configurationService.get(key);
    }

    @Override
    public <T> T getDefault(Key<T> key) {
        T result = null;
        try {
            result = super.getDefault(key);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return result == null ? configurationService.getDefault(key) : result;
    }

    @Override
    public void load() {
        configurationService.load();
        super.load();
    }

    @Override
    public String toString() {
        if (configurationService == null) {
            return super.toString();
        } else {
            return super.toString() + "\n" + configurationService;
        }
    }
}
