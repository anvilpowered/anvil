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

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class EnvironmentManagerImpl implements EnvironmentManager{

    private final Map<String, Environment> environments;
    final Map<Object, Set<Environment>> pluginEnvironmentMap;

    public EnvironmentManagerImpl() {
        environments = new HashMap<>();
        pluginEnvironmentMap = new HashMap<>();
    }

    @Override
    public Environment getCoreEnvironment() {
        return Preconditions.checkNotNull(environments.get("anvil"),
            "Global environment not loaded");
    }

    @Override
    public Environment getEnvironmentUnsafe(String name) {
        return Preconditions.checkNotNull(environments.get(name),
            "Could not find environment " + name);
    }

    @Override
    public Map<String, Environment> getEnvironments() {
        return Collections.unmodifiableMap(environments);
    }

    @Override
    public Stream<Environment> getEnvironmentsAsStream(Pattern pattern) {
        return environments.entrySet().stream().filter(e ->
            pattern.matcher(e.getKey()).matches()
        ).map(Map.Entry::getValue);
    }

    @Override
    public List<Environment> getEnvironments(Pattern pattern) {
        return getEnvironmentsAsStream(pattern).collect(Collectors.toList());
    }

    @Override
    public Optional<Environment> getEnvironment(Pattern pattern) {
        return getEnvironmentsAsStream(pattern).findAny();
    }

    @Override
    public Optional<Environment> getEnvironment(String name) {
        return Optional.ofNullable(environments.get(name));
    }

    void registerEnvironment(Environment environment, Object plugin) {
        final String name = environment.getName();
        if (environments.containsKey(name)) {
            throw new IllegalArgumentException("Environment " + name + " already exists");
        }
        environments.put(name, environment);
        Set<Environment> envs = pluginEnvironmentMap.get(plugin);
        if (envs == null) {
            envs = new TreeSet<>();
            envs.add(environment);
            pluginEnvironmentMap.put(plugin, envs);
        } else {
            envs.add(environment);
        }
    }
}
