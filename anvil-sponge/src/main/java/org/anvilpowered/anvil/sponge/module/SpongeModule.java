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

package org.anvilpowered.anvil.sponge.module;

import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.config.AnvilCoreConfigurationService;
import org.anvilpowered.anvil.common.module.CommonModule;
import org.anvilpowered.anvil.sponge.command.SpongeAnvilCommandNode;
import org.anvilpowered.anvil.sponge.config.AnvilCoreSpongeConfigurationService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class SpongeModule extends CommonModule<Text, CommandSource> {

    @Override
    protected void configure() {
        super.configure();

        bind(AnvilCoreConfigurationService.class).to(AnvilCoreSpongeConfigurationService.class);
        bind(new TypeLiteral<CommandNode<CommandSpec>>() {
        }).to(SpongeAnvilCommandNode.class);
    }
}
