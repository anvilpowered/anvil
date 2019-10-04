package rocks.milspecsg.msrepository.api.manager;

import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;


public interface Manager<TKey, T extends ObjectWithId<TKey>, R extends Repository<TKey, T>> {

    R getPrimaryRepository();

}
