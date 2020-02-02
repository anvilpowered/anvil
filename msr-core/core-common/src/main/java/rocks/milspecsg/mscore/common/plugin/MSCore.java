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

import com.google.inject.Injector;
import rocks.milspecsg.mscore.api.coremember.CoreMemberManager;
import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.mscore.api.plugin.PluginMessages;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;

import java.util.Objects;

public abstract class MSCore {

    protected static Injector injector;

    public static Injector getInjector() {
        try {
            return Objects.requireNonNull(injector);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Injector has not been loaded yet!", e);
        }
    }

    public static CoreMemberManager getCoreMemberManager() {
        return injector.getInstance(CoreMemberManager.class);
    }

    public static CoreMemberRepository<?, ?> getCoreMemberRepository() {
        return getCoreMemberManager().getPrimaryComponent();
    }

    public static Registry getRegistry() {
        return injector.getInstance(Registry.class);
    }

    public static <TString> PluginInfo<TString> getPluginInfo() {
        return MSRepository.getCoreEnvironment().getPluginInfo();
    }

    public static <TString> PluginMessages<TString> getPluginMessages() {
        return MSRepository.getCoreEnvironment().getInstance("rocks.milspecsg.mscore.api.plugin.PluginMessages");
    }

    protected void load() {
        injector.getInstance(Registry.class).load();
    }
}
