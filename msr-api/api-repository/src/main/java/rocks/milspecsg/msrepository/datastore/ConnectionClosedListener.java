package rocks.milspecsg.msrepository.datastore;

@FunctionalInterface
public interface ConnectionClosedListener<TDataStore> {
    void closed(TDataStore dataStore);
}
