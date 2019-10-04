package rocks.milspecsg.msrepository.datastore;

@FunctionalInterface
public interface ConnectionOpenedListener<TDataStore> {
    void loaded(TDataStore dataStore);
}
