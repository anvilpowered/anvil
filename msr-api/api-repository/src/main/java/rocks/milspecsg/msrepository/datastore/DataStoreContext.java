/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        requestCloseConnection();
        this.dataStore = dataStore;
        notifyConnectionOpenedListeners(dataStore);
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

    private void notifyConnectionOpenedListeners(TDataStore dataStore) {
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
