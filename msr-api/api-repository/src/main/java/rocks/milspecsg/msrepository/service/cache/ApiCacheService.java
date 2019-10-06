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

package rocks.milspecsg.msrepository.service.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ApiCacheService<T> implements CacheService<T> {

    protected ConfigurationService configurationService;

    protected Map<T, Long> cache;

    Integer intervalSeconds = null;
    Integer timeoutSeconds = null;

    public ApiCacheService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
        cache = new HashMap<>();
    }

    private void configLoaded(Object plugin) {
        stopCacheInvalidationTask();
        intervalSeconds = configurationService.getConfigInteger(ConfigKeys.CACHE_INVALIDATION_INTERVAL_SECONDS_INT);
        timeoutSeconds = configurationService.getConfigInteger(ConfigKeys.CACHE_INVALIDATION_TIMOUT_SECONDS_INT);
        startCacheInvalidationTask();
    }

    /**
     * Cache invalidation task
     */
    @Override
    public Runnable getCacheInvalidationTask() {
        return () -> {

            List<T> toRemove = new ArrayList<>();
            for (T t : getAll()) {
                if (System.currentTimeMillis() - cache.get(t) > timeoutSeconds * 1000L) {
                    // if time has gone over limit
                    toRemove.add(t);
                    //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Removing ", t));
                }
            }
            // remove from map
            toRemove.forEach(this::delete);
        };
    }

    @Override
    public Set<T> getAll() {
        return cache.keySet();
    }

    @Override
    public Optional<T> put(T t) {
        if (t == null) return Optional.empty();
        cache.put(t, System.currentTimeMillis());
        return Optional.of(t);
    }

    @Override
    public List<T> put(List<T> list) {
        return list.stream().map(t -> put(t).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Optional<T> deleteOne(Predicate<? super T> predicate) {
        return getOne(predicate).flatMap(this::delete);
    }

    @Override
    public Optional<T> delete(T t) {
        return cache.remove(t) == null ? Optional.empty() : Optional.of(t);
    }

    @Override
    public List<T> delete(Predicate<? super T> predicate) {
        return cache.keySet().stream().filter(predicate).filter(t -> cache.remove(t) != null).collect(Collectors.toList());
    }

    @Override
    public List<T> getAll(Predicate<? super T> predicate) {
        return getAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Optional<T> getOne(Predicate<? super T> predicate) {
        return getAll(predicate).stream().findAny();
    }
}
