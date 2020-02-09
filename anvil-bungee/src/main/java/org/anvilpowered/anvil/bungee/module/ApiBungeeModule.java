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

package org.anvilpowered.anvil.bungee.module;

import com.google.inject.TypeLiteral;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.StringResult;
import org.anvilpowered.anvil.bungee.util.BungeeStringResult;
import org.anvilpowered.anvil.common.module.ApiCommonModule;

public class ApiBungeeModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<StringResult<TextComponent, CommandSender>>() {
        }).to(BungeeStringResult.class);
    }
}
