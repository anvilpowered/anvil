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

    public Supplier<Optional<T>> ifNotPresent(Function<? super RepositoryCacheService<TKey, T>, Optional<T>> fromCache, Supplier<Optional<T>> fromDB) {
        Optional<T> main = fromCache == null ? Optional.empty() : fromCache.apply(this);
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
