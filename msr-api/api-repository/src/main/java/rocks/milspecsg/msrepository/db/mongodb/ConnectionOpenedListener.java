package rocks.milspecsg.msrepository.db.mongodb;

import org.mongodb.morphia.Datastore;

@FunctionalInterface
public interface ConnectionOpenedListener {

    void loaded(Datastore datastore);
}
