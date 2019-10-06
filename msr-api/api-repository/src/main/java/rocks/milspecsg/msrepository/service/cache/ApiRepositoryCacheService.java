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
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ApiRepositoryCacheService<TKey, T extends ObjectWithId<TKey>> extends ApiCacheService<T> implements RepositoryCacheService<TKey, T> {

    public ApiRepositoryCacheService(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public Optional<T> getOne(TKey id) {
        return getOne(dbo -> dbo.getId().equals(id));
    }

    @Override
    public Optional<T> deleteOne(TKey id) {
        return deleteOne(dbo -> dbo.getId().equals(id));
    }

    @Override
    public Supplier<List<T>> save(Supplier<List<T>> fromDB) {
        return () -> put(fromDB.get());
    }

    @Override
    public List<T> save(List<T> fromDB) {
        return save(() -> fromDB).get();
    }

    @Override
    public Optional<T> save(T fromDB) {
        return save(Collections.singletonList(fromDB)).stream().findAny();
    }

    public <C extends RepositoryCacheService<TKey, T>> Supplier<Optional<T>> ifNotPresent(Function<C, Optional<T>> fromCache, Supplier<Optional<T>> fromDB) {
        Optional<T> main = fromCache == null ? Optional.empty() : fromCache.apply((C) this);
        if (main.isPresent()) {
            return () -> main;
        } else {
            return () -> fromDB.get().flatMap(this::put);
        }
    }

    @Override
    public Supplier<Optional<T>> ifNotPresent(TKey id, Supplier<Optional<T>> fromDB) {
        return ifNotPresent(cache -> cache.getOne(id), fromDB);
    }

}
