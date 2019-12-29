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

package rocks.milspecsg.msrepository.service.common.cache;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;
import rocks.milspecsg.msrepository.service.common.component.CommonComponent;
import rocks.milspecsg.msrepository.service.common.storageservice.CommonStorageService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CommonCacheService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonComponent<TKey, TDataStore, TDataStoreConfig>
    implements CacheService<TKey, T, TDataStore, TDataStoreConfig>,
    CommonStorageService<TKey, T, TDataStore, TDataStoreConfig> {

    protected ConfigurationService configurationService;

    protected ConcurrentMap<T, Long> cache;

    private Integer timeoutSeconds;

    protected CommonCacheService(DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext, ConfigurationService configurationService) {
        super(dataStoreContext);
        this.configurationService = configurationService;
        this.configurationService.addConfigLoadedListener(this::configLoaded);
        cache = new ConcurrentHashMap<>();
    }

    private void configLoaded(Object plugin) {
        stopCacheInvalidationTask();
        Integer intervalSeconds = configurationService.getConfigInteger(ConfigKeys.CACHE_INVALIDATION_INTERVAL_SECONDS_INT);
        timeoutSeconds = configurationService.getConfigInteger(ConfigKeys.CACHE_INVALIDATION_TIMOUT_SECONDS_INT);
        startCacheInvalidationTask(intervalSeconds);
    }

    @Override
    public Runnable getCacheInvalidationTask() {
        return () -> {
            List<T> toRemove = new ArrayList<>();
            for (T t : getAll()) {
                if (System.currentTimeMillis() - cache.get(t) > timeoutSeconds * 1000L) {
                    toRemove.add(t);
                }
            }
            toRemove.forEach(this::delete);
        };
    }

    @Override
    public Set<T> getAll() {
        return cache.keySet();
    }

    @Override
    public CompletableFuture<Optional<T>> insertOne(T t) {
        return CompletableFuture.supplyAsync(() -> {
            if (t == null) return Optional.empty();
            cache.put(t, System.currentTimeMillis());
            return Optional.of(t);
        });
    }

    @Override
    public CompletableFuture<List<T>> insert(List<T> list) {
        return CompletableFuture.supplyAsync(() -> list.stream().map(t -> insertOne(t).join().orElse(null)).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Optional<T>> getOne(TKey id) {
        return CompletableFuture.supplyAsync(() -> getOne(dbo -> dbo.getId().equals(id)));
    }

    @Override
    public CompletableFuture<Boolean> deleteOne(TKey id) {
        return CompletableFuture.supplyAsync(() -> deleteOne(dbo -> dbo.getId().equals(id)).isPresent());
    }

    @Override
    public CompletableFuture<List<TKey>> getAllIds() {
        return CompletableFuture.supplyAsync(() -> cache.keySet().stream().map(ObjectWithId::getId).collect(Collectors.toList()));
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
