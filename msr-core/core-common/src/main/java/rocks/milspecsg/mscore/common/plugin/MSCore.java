/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.mscore.common.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import rocks.milspecsg.mscore.api.coremember.CoreMemberManager;
import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.mscore.api.plugin.PluginMessages;
import rocks.milspecsg.msrepository.api.Environment;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.plugin.Plugin;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;

import java.util.Objects;

public abstract class MSCore<TPluginContainer> implements Plugin<TPluginContainer> {

    @Inject
    protected TPluginContainer pluginContainer;

    protected static Environment environment;

    public MSCore(Injector injector, Module... modules) {
        MSRepository.environmentBuilder()
            .setName(MSCorePluginInfo.id)
            .setRootInjector(injector)
            .addModules(modules)
            .whenReady(e -> environment = e)
            .register(this);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return MSCorePluginInfo.id;
    }

    @Override
    public TPluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Override
    public Environment getPrimaryEnvironment() {
        return getEnvironment();
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
