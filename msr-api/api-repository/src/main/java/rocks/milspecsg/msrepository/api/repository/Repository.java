package rocks.milspecsg.msrepository.api.repository;

import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Repository<TKey, T extends ObjectWithId<TKey>, C extends RepositoryCacheService<TKey, T>> {

    /**
     * @return An empty {@link T}
     */
    T generateEmpty();

    /**
     * @param item Object to insert
     * @return The inserted item with {@link T#getId()} ()} set
     */
    CompletableFuture<Optional<T>> insertOneIntoDS(T item);

    /**
     * @param item Object to insert
     * @return The inserted item with {@link T#getId()} ()} set
     */
    CompletableFuture<Optional<T>> insertOne(T item);

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id}
     */
    CompletableFuture<Optional<T>> getOneFromDS(TKey id);

    /**
     * @param id {@link TKey} to query repository with
     * @return The first item that satisfies {@link T#getId()} == {@param id}
     */
    CompletableFuture<Optional<T>> getOne(TKey id);

    /**
     * @return A list of all {@link TKey} ids in the repository
     */
    CompletableFuture<List<TKey>> getAllIds();

    /**
     * Deletes the first item that satisfies {@link T#getId()} == {@param id}
     *
     * @param id {@link TKey} to query repository with
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> deleteOneFromDS(TKey id);

    /**
     * Deletes the first item that satisfies {@link T#getId()} == {@param id}
     *
     * @param id {@link TKey} to query repository with
     * @return Whether or not an item was found and deleted
     */
    CompletableFuture<Boolean> deleteOne(TKey id);

    default Optional<C> getRepositoryCacheService() {
        return Optional.empty();
    }

    CompletableFuture<Optional<T>> ifNotPresent(Function<C, Optional<T>> fromCache, Supplier<Optional<T>> fromDB);

}