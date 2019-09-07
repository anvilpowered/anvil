package rocks.milspecsg.msrepository.api;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.DeleteOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msrepository.model.Dbo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Repository<T extends Dbo> {

    /**
     * Represents the default singular identifier for a {@link Dbo}
     * <p>
     * Should be overridden by other plugins who change the name of the object.
     * Examples: "Clan", "Faction", "Guild", "Member", ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierSingularUpper();

    /**
     * Represents the default plural identifier for a {@link Dbo}
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "Clans", "Factions", "Guilds", "Members" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierPluralUpper();

    /**
     * Represents the default singular identifier for a {@link Dbo}
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clan", "faction", "guild", "member" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     *
     * <p>
     * note: this will be used as the base command
     * </p>
     */
    String getDefaultIdentifierSingularLower();

    /**
     * Represents the default plural identifier for a {@link Dbo}
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clans", "factions", "guilds", "members" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierPluralLower();

    /**
     * @return An empty {@link T}
     */
    T generateEmpty();

    CompletableFuture<Optional<T>> insertOne(T item);

    CompletableFuture<Optional<T>> getOne(ObjectId id);

    CompletableFuture<WriteResult> delete(Query<T> query, DeleteOptions deleteOptions);

    CompletableFuture<WriteResult> delete(Query<T> query);

    CompletableFuture<WriteResult> deleteOne(ObjectId id);


    UpdateOperations<T> createUpdateOperations();

    UpdateOperations<T> inc(String field, Number value);

    UpdateOperations<T> inc(String field);

    Query<T> asQuery();

    Query<T> asQuery(ObjectId id);

    static <U> Optional<U> single(List<U> list) {
        return list.size() > 0 ? Optional.ofNullable(list.get(0)) : Optional.empty();
    }

    static <U> Function<List<U>, Optional<U>> single() {
        return Repository::single;
    }

    <R extends RepositoryCacheService<T>> Supplier<List<T>> saveToCache(R repositoryCacheService, Supplier<List<T>> fromDB);

    <R extends RepositoryCacheService<T>> Supplier<Optional<T>> ifNotPresent(R repositoryCacheService, Function<R, Optional<T>> fromCache, Supplier<Optional<T>> fromDB);

    Supplier<Optional<T>> ifNotPresent(RepositoryCacheService<T> repositoryCacheService, ObjectId id);

}