package rocks.milspecsg.msrepository.api.cache;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface CacheService<T> {

    /**
     * Starts cache invalidation task
     */
    void startCacheInvalidationTask();

    /**
     * Stop cache invalidation task
     */
    void stopCacheInvalidationTask();

    /**
     *
     * @return Cache invalidation task
     */
    Runnable getCacheInvalidationTask();

    /**
     * Add a {@link T} to the cache
     *
     * Checks whether object is already in cache
     *
     * @param t {@link T} to put to cache
     * @return {@link Optional} with inserted {@link T} if successfully inserted.
     * Otherwise {@link Optional#empty()}
     */
    Optional<T> put(T t);

    /**
     * Checks whether object is already in cache
     *
     * @param list {@link List<T>} to insert into cache
     * @return {@link List<T>} containing all {@link T} that were successfully inserted
     */
    List<T> put(List<T> list);

    /**
     * @return A set containing all parties in the cache
     */
    Set<T> getAll();

    /**
     * Deletes a {@link T} from the cache
     *
     * @param predicate of {@link T} to remove from cache
     * @return An optional containing the {@link T} if it was successfully removed
     */
    Optional<T> deleteOne(Predicate<? super T> predicate);

    /**
     * Deletes a {@link T} from the cache
     *
     * @param t {@link T} to remove from cache
     * @return An optional containing the {@link T} if it was successfully removed
     */
    Optional<T> delete(T t);

    /**
     * Deletes a {@link T} from the cache
     *
     * @param predicate of {@link T} to remove from cache
     * @return A list of successfully deleted elements
     */
    List<T> delete(Predicate<? super T> predicate);

    List<T> getAll(Predicate<? super T> predicate);

    Optional<T> getOne(Predicate<? super T> predicate);

}
