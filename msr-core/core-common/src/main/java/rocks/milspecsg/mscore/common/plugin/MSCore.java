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

import com.google.inject.Binding;
import com.google.inject.Injector;
import rocks.milspecsg.mscore.api.coremember.CoreMemberManager;
import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.mscore.api.plugin.PluginMessages;
import rocks.milspecsg.msrepository.api.data.registry.Registry;

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

    @SuppressWarnings("unchecked")
    public static <T> T getService(String name, Injector injector) {
        Binding<?>[] binding = {null};
        injector.getBindings().forEach((k, v) -> {
            if (k.getTypeLiteral().getType().getTypeName().contains(name)) {
                binding[0] = v;
            }
        });
        return (T) Objects.requireNonNull(binding[0].getProvider().get(), "Could not find binding for service: " + name + " in injector " + injector);
    }

    public static <T> T getService(String name) {
        return getService(name, injector);
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

    public static <TString> PluginMessages<TString> getPluginMessages() {
        return getService("rocks.milspecsg.mscore.api.plugin.PluginMessages");
    }

    protected void load() {
        injector.getInstance(Registry.class).load(this);
    }
}
