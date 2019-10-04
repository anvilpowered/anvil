package rocks.milspecsg.msrepository.api.repository;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

public interface JsonRepository<T extends ObjectWithId<ObjectId>> extends Repository<ObjectId, T> {
}
