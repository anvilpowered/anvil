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

package org.anvilpowered.anvil.sponge.util;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.util.CurrentServerService;

import java.util.Optional;
import java.util.UUID;

public class SpongeCurrentServerService implements CurrentServerService {

    @Inject
    protected Registry registry;

    @Override
    public Optional<String> getName(UUID userUUID) {
        return Optional.of(registry.getOrDefault(Keys.SERVER_NAME));
    }

    @Override
    public Optional<String> getName(String userName) {
        return Optional.of(registry.getOrDefault(Keys.SERVER_NAME));
    }
}
