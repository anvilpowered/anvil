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

package org.anvilpowered.anvil.core.common.plugin;

import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.common.plugin.CommonPlugin;
import org.anvilpowered.anvil.core.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.core.api.coremember.repository.CoreMemberRepository;
import org.anvilpowered.anvil.core.api.plugin.PluginMessages;

import java.util.Objects;

public abstract class AnvilCore<TPluginContainer> extends CommonPlugin<TPluginContainer> {

    protected static Environment environment;

    public AnvilCore(Injector injector, Module... modules) {
        super(AnvilCorePluginInfo.id, e -> AnvilCore.environment = e, injector, modules);
    }

    public static Environment getEnvironment() {
        return Objects.requireNonNull(environment, "Environment has not been loaded yet!");
    }

    public static CoreMemberManager getCoreMemberManager() {
        return environment.getInjector().getInstance(CoreMemberManager.class);
    }

    public static CoreMemberRepository<?, ?> getCoreMemberRepository() {
        return getCoreMemberManager().getPrimaryComponent();
    }

    public static Registry getRegistry() {
        return environment.getInjector().getInstance(Registry.class);
    }

    public static <TString> PluginInfo<TString> getPluginInfo() {
        return environment.getPluginInfo();
    }

    public static <TString> PluginMessages<TString> getPluginMessages() {
        return environment.getInstance(PluginMessages.class.getCanonicalName());
    }
}
