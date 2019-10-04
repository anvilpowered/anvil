package rocks.milspecsg.msrepository.service.apirepository;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.api.repository.JsonRepository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

public abstract class ApiJsonRepository<T extends ObjectWithId<ObjectId>> extends ApiRepository<ObjectId, T, Object> implements JsonRepository<T> {

    public ApiJsonRepository(DataStoreContext<Object> dataStoreContext) {
        super(dataStoreContext);
    }
}
