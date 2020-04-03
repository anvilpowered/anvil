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

package org.anvilpowered.anvil.api;

import com.google.inject.Binder;
import com.google.inject.Binding;
import org.anvilpowered.anvil.api.data.key.Key;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.BindingExtensions;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused"})
public final class Anvil {

    static final Map<Long, Binding<?>> bindingsCache = new HashMap<>();
    private static ServiceManager serviceManager;

    private Anvil() {
        throw new AssertionError("**boss music** No instance for you!");
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
        return getServiceManager().provide(Platform.class);
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

    public static <T> T resolveForSharedEnvironment(Key<T> key, Registry registry) {
        Registry coreRegistry = getEnvironmentManager().getCoreEnvironment().getRegistry();
        if (registry.getOrDefault(Keys.USE_SHARED_ENVIRONMENT)) {
            if (key.equals(Keys.DATA_STORE_NAME)
                || key.equals(Keys.MONGODB_HOSTNAME)
                || key.equals(Keys.MONGODB_PORT)
                || key.equals(Keys.MONGODB_USE_SRV)) {
                return coreRegistry.getOrDefault(key);
            } else if (registry.getOrDefault(Keys.USE_SHARED_CREDENTIALS)) {
                if (key.equals(Keys.MONGODB_USE_CONNECTION_STRING)
                    || key.equals(Keys.MONGODB_CONNECTION_STRING)
                    || key.equals(Keys.MONGODB_USERNAME)
                    || key.equals(Keys.MONGODB_PASSWORD)
                    || key.equals(Keys.MONGODB_AUTH_DB)
                    || key.equals(Keys.MONGODB_USE_AUTH)) {
                    return coreRegistry.getOrDefault(key);
                }
            }
        }
        return registry.getOrDefault(key);
    }
}
