/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.base.datastore;

import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.datastore.CacheService;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseCacheService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends BaseComponent<TKey, TDataStore>
    implements CacheService<TKey, T, TDataStore>,
    BaseStorageService<TKey, T, TDataStore> {

    protected Registry registry;

    protected ConcurrentMap<T, Long> cache;

    private Integer timeoutSeconds;

    protected BaseCacheService(DataStoreContext<TKey, TDataStore> dataStoreContext, Registry registry) {
        super(dataStoreContext);
        this.registry = registry;
        registry.whenLoaded(this::registryLoaded);
        cache = new ConcurrentHashMap<>();
    }

    private void registryLoaded() {
        stopCacheInvalidationTask();
        Integer intervalSeconds = registry.getOrDefault(Keys.CACHE_INVALIDATION_INTERVAL_SECONDS);
        timeoutSeconds = registry.getOrDefault(Keys.CACHE_INVALIDATION_TIMOUT_SECONDS);
        startCacheInvalidationTask(intervalSeconds);
    }

    @Override
    public Runnable getCacheInvalidationTask() {
        return () -> {
            List<T> toRemove = new ArrayList<>();
            for (T t : getAllAsSet()) {
                if (System.currentTimeMillis() - cache.get(t) > timeoutSeconds * 1000L) {
                    toRemove.add(t);
                }
            }
            toRemove.forEach(this::delete);
        };
    }

    @Override
    public Set<T> getAllAsSet() {
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
        return getAllAsSet().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Optional<T> getOne(Predicate<? super T> predicate) {
        return getAll(predicate).stream().findAny();
    }
}
