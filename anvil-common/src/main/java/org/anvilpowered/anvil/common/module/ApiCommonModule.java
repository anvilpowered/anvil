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

package org.anvilpowered.anvil.common.module;

import com.google.inject.AbstractModule;
import org.anvilpowered.anvil.api.entity.RestrictionService;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.redis.RedisService;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.anvil.common.entity.CommonRestrictionService;
import org.anvilpowered.anvil.common.redis.CommonRedisService;
import org.anvilpowered.anvil.common.util.CommonTimeFormatService;

public class ApiCommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TimeFormatService.class).to(CommonTimeFormatService.class);
        bind(RedisService.class).to(CommonRedisService.class);
        bind(RestrictionService.class).toProvider(
            BindingExtensions.asInternalProvider(CommonRestrictionService.class));
    }
}
