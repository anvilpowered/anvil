package rocks.milspecsg.msrepository.api.cache;

import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RepositoryCacheService<TKey, T extends ObjectWithId<TKey>> extends CacheService<T> {

    Optional<T> getOne(TKey id);

    Optional<T> deleteOne(TKey id);

    /**
     * @param fromDB {@link Supplier<List>} that retrieves data from datastore
     * @return A list containing all elements that were successfully retrieved from the datastore and saved to the cache
     */
    Supplier<List<T>> save(Supplier<List<T>> fromDB);

    /**
     * @param fromDB {@link List<T>} retrieved from the datastore
     * @return A list containing all elements that were successfully retrieved from the datastore and saved to the cache
     */
    List<T> save(List<T> fromDB);

    /**
     * @param fromDB {@link T} retrieved from the datastore
     * @return An optional containing the saved element
     */
    Optional<T> save(T fromDB);

    /**
     * @param fromCache {@link Supplier} that retrieves data from cache
     * @param fromDB    {@link Supplier} that retrieves data from datastore
     * @return Attempts to retrieve an item from the cache. If the item is not found, retrieve it from the datastore
     */
    <C extends RepositoryCacheService<TKey, T>> Supplier<Optional<T>> ifNotPresent(Function<C, Optional<T>> fromCache, Supplier<Optional<T>> fromDB);

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id} from the cache.
     * If no matching item is present in the cache, return the first item that
     * satisfies {@link T#getId()} == {@param id} from the datastore
     */
    Supplier<Optional<T>> ifNotPresent(TKey id, Supplier<Optional<T>> fromDB);

}
