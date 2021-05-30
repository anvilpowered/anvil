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

package org.anvilpowered.anvil.velocity.command;

import com.google.inject.Singleton;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.TextComponent;
import org.anvilpowered.anvil.common.command.CommonCallbackCommand;

import java.util.List;

@Singleton
public class VelocityCallbackCommand
    extends CommonCallbackCommand<TextComponent, CommandSource>
    implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        super.execute(invocation.source(), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return null;
    }
}
