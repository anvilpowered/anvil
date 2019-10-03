package rocks.milspecsg.msrepository.api;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.model.data.dbo.MongoDbo;

import java.util.Optional;

public interface RepositoryCacheService<T extends MongoDbo> extends CacheInvalidationService<T> {

    Optional<T> getOne(ObjectId id);

}
