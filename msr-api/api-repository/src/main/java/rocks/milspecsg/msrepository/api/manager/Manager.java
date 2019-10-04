package rocks.milspecsg.msrepository.api.manager;

import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;


public interface Manager<T extends ObjectWithId<?>, R extends Repository<?, T>> {

    R getPrimaryRepository();

}
