package rocks.milspecsg.msrepository.api;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.model.Dbo;

import java.util.Optional;

public interface RepositoryCacheService<T extends Dbo> extends CacheInvalidationService<T> {

    Optional<T> getOne(ObjectId id);

}
