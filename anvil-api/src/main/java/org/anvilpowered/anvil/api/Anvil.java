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

package org.anvilpowered.anvil.api;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.core.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.core.coremember.repository.CoreMemberRepository;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.base.plugin.BasePlugin;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused"})
public class Anvil extends BasePlugin {

    static final Map<Long, Binding<?>> bindingsCache = new HashMap<>();
    protected static ServiceManager serviceManager;
    protected static Environment environment;

    Anvil(String name, Injector rootInjector, Module module) {
        super(name, rootInjector, module);
    }

    public static BindingExtensions getBindingExtensions(Binder binder) {
        return getServiceManager().provide(BindingExtensions.class, binder);
    }

    public static Environment.Builder getEnvironmentBuilder() {
        return getServiceManager().provide(Environment.Builder.class);
    }

    public static EnvironmentManager getEnvironmentManager() {
        return getServiceManager().provide(EnvironmentManager.class);
    }

    public static Platform getPlatform() {
        return environment.getInjector().getInstance(Platform.class);
    }

    public static Registry getRegistry() {
        return environment.getInjector().getInstance(Registry.class);
    }

    public static CoreMemberManager getCoreMemberManager() {
        return environment.getInjector().getInstance(CoreMemberManager.class);
    }

    public static CoreMemberRepository<?, ?> getCoreMemberRepository() {
        return getCoreMemberManager().getPrimaryComponent();
    }

    public static ServiceManager getServiceManager() {
        if (serviceManager != null) {
            return serviceManager;
        }
        try {
            return serviceManager = (ServiceManager)
                Class.forName("org.anvilpowered.anvil.api.ServiceManagerImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException("Could not find ServiceManager implementation!", e);
        }
    }
}
