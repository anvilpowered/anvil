/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.api.data;

import com.google.inject.Binding;
import com.google.inject.Injector;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.util.PluginInfo;

import java.util.Objects;

public interface Environment {

    @SuppressWarnings("unchecked")
    static <T> T getService(String name, Injector injector) {
        Binding<?>[] binding = {null};
        injector.getBindings().forEach((k, v) -> {
            if (k.getTypeLiteral().getType().getTypeName().contains(name)) {
                binding[0] = v;
            }
        });
        return Objects.requireNonNull(
            (Binding<T>) binding[0],
            "Could not find binding for service: " + name + " in injector " + injector
        ).getProvider().get();
    }

    default <T> T getService(String name) {
        return getService(name, getInjector());
    }

    String getName();

    Injector getInjector();

    <TString> PluginInfo<TString> getPluginInfo();

    Registry getRegistry();
}
