package rocks.milspecsg.msrepository.api.manager;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.api.repository.MongoRepository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;

public interface MongoManager<T extends ObjectWithId<ObjectId>> extends Manager<ObjectId, T> {

    default Optional<MongoRepository<T>> getMongoRepository() {
        return Optional.empty();
    }

}
