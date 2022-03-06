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

package org.anvilpowered.anvil.common.command;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CommonAnvilCommandNode<TCommandExecutor, TCommandSource>
    implements CommandNode<TCommandSource> {

    protected static final List<String> PLUGINS_ALIAS = Collections.singletonList("plugins");
    protected static final List<String> RELOAD_ALIAS = Collections.singletonList("reload");
    protected static final List<String> REGEDIT_ALIAS = Collections.singletonList("regedit");
    protected static final List<String> HELP_ALIAS = Collections.singletonList("help");
    protected static final List<String> VERSION_ALIAS = Collections.singletonList("version");

    protected static final String PLUGINS_DESCRIPTION
        = String.format("%s plugins command", AnvilPluginInfo.name);
    protected static final String RELOAD_DESCRIPTION
        = String.format("%s reload command", AnvilPluginInfo.name);
    protected static final String REGEDIT_DESCRIPTION
        = String.format("%s regedit command", AnvilPluginInfo.name);
    protected static final String HELP_DESCRIPTION
        = String.format("%s help command", AnvilPluginInfo.name);
    protected static final String VERSION_DESCRIPTION
        = String.format("%s version command", AnvilPluginInfo.name);
     protected static final String ROOT_DESCRIPTION
        = String.format("%s root command", AnvilPluginInfo.name);

    protected static final String RELOAD_USAGE = "[-a|--all|-r|--regex] [<plugin>]";
    protected static final String REGEDIT_USAGE
        = "[cancel|select|commit|start|key] [<reg>|<env>|<key>] [info|set|unset|unstage] [<value>]";

    protected static final String HELP_COMMAND = "/anvil help";

    private boolean alreadyLoaded;
    protected Map<List<String>, Function<TCommandSource, String>> descriptions;
    protected Map<List<String>, Predicate<TCommandSource>> permissions;
    protected Map<List<String>, Function<TCommandSource, String>> usages;

    @Inject
    protected CommandService<TCommandExecutor, TCommandSource> commandService;

    @Inject
    protected Environment environment;

    protected Registry registry;

    protected CommonAnvilCommandNode(Registry registry) {
        this.registry = registry;
        registry.whenLoaded(() -> {
            if (alreadyLoaded) return;
            loadCommands();
            alreadyLoaded = true;
        }).register();
        alreadyLoaded = false;
        descriptions = new HashMap<>();
        permissions = new HashMap<>();
        usages = new HashMap<>();
        descriptions.put(PLUGINS_ALIAS, c -> PLUGINS_DESCRIPTION);
        descriptions.put(RELOAD_ALIAS, c -> RELOAD_DESCRIPTION);
        descriptions.put(REGEDIT_ALIAS, c -> REGEDIT_DESCRIPTION);
        descriptions.put(HELP_ALIAS, c -> HELP_DESCRIPTION);
        descriptions.put(VERSION_ALIAS, c -> VERSION_DESCRIPTION);
        usages.put(RELOAD_ALIAS, c -> RELOAD_USAGE);
        usages.put(REGEDIT_ALIAS, c -> REGEDIT_USAGE);
    }

    protected abstract void loadCommands();

    private static final String ERROR_MESSAGE = "Anvil command has not been loaded yet";

    @Nullable
    private static String alias;

    public static String getAlias() {
        if (alias == null) {
            switch (Anvil.getPlatform().getName()) {
                case "bungee":
                    alias = "anvilb";
                    break;
                case "velocity":
                    alias = "anvilv";
                    break;
                default:
                    alias = "anvil";
                    break;
            }
        }
        return alias;
    }

    @Override
    public Map<List<String>, Function<TCommandSource, String>> getDescriptions() {
        return Preconditions.checkNotNull(descriptions, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Predicate<TCommandSource>> getPermissions() {
        return Preconditions.checkNotNull(permissions, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Function<TCommandSource, String>> getUsages() {
        return Preconditions.checkNotNull(usages, ERROR_MESSAGE);
    }

    @Override
    public String getName() {
        return getAlias();
    }
}
