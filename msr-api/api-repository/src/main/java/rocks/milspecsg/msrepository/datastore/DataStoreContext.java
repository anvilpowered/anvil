package rocks.milspecsg.msrepository.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DataStoreContext<TDataStore> {

    private TDataStore dataStore = null;
    private final List<ConnectionOpenedListener<TDataStore>> connectionOpenedListeners;
    private final List<ConnectionClosedListener<TDataStore>> connectionClosedListeners;

    public DataStoreContext() {
        this.connectionOpenedListeners = new ArrayList<>();
        this.connectionClosedListeners = new ArrayList<>();
    }

    abstract public void init(String hostname, int port, String dbName, String username, String password, boolean useAuth);

    protected final void setDataStore(TDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public final Optional<TDataStore> getDataStore() {
        return Optional.ofNullable(dataStore);
    }

    abstract protected void closeConnection(TDataStore dataStore);

    public final void requestCloseConnection() {
        if (dataStore != null) {
            notifyConnectionClosedListeners(dataStore);
            closeConnection(dataStore);
        }
    }

    protected final void notifyConnectionOpenedListeners(TDataStore dataStore) {
        connectionOpenedListeners.forEach(listener -> listener.loaded(dataStore));
    }

    public final void addConnectionOpenedListener(ConnectionOpenedListener<TDataStore> connectionOpenedListener) {
        connectionOpenedListeners.add(connectionOpenedListener);
    }

    public final void removeConnectionOpenedListener(ConnectionOpenedListener<TDataStore> connectionOpenedListener) {
        connectionOpenedListeners.remove(connectionOpenedListener);
    }

    private void notifyConnectionClosedListeners(TDataStore dataStore) {
        connectionClosedListeners.forEach(listener -> listener.closed(dataStore));
    }

    public final void addConnectionClosedListener(ConnectionClosedListener<TDataStore> connectionClosedListener) {
        connectionClosedListeners.add(connectionClosedListener);
    }

    public final void removeConnectionClosedListener(ConnectionClosedListener<TDataStore> connectionClosedListener) {
        connectionClosedListeners.remove(connectionClosedListener);
    }
}
