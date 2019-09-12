package rocks.milspecsg.msrepository.db.mongodb;

import org.mongodb.morphia.Datastore;

@FunctionalInterface
public interface ConnectionClosedListener {
    void closed(Datastore datastore);
}
