package rocks.milspecsg.msrepository.api.repository;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.concurrent.CompletableFuture;

public interface MariaRepository<T extends ObjectWithId<ObjectId>> extends Repository<ObjectId, T> {

}
