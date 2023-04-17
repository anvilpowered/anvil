/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.agent;

import com.google.inject.Inject;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.anvilpowered.anvil.user.MyCommandSource;
import org.anvilpowered.kbrig.brigadier.BrigadierConverter;
import org.anvilpowered.kbrig.tree.SourceConverter;

public class ExamplePlugin {

    @Inject
    private ProxyServer proxyServer;

    @Subscribe
    void onProxyInit(ProxyInitializeEvent event) {
class BridgeSource implements MyCommandSource {
    private final CommandSource velocityCommandSource;

    BridgeSource(CommandSource velocityCommandSource) {
        this.velocityCommandSource = velocityCommandSource;
    }

    public void sendMessage(Component message) {
        velocityCommandSource.sendMessage(message);
    }
}

final LiteralCommandNode<CommandSource> mappedSourceNode =
    BrigadierConverter.toBrigadier(
        SourceConverter.mapSource(CustomCommand.createPing(), BridgeSource::new)
    );

proxyServer.getCommandManager().register(new BrigadierCommand(mappedSourceNode));

    }
}
