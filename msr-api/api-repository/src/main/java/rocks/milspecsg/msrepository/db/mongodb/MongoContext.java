package rocks.milspecsg.msrepository.db.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class MongoContext {

    private Datastore datastore = null;

    public MongoContext() {
        this.connectionOpenedListeners = new ArrayList<>();
        this.connectionClosedListeners = new ArrayList<>();
    }

    private List<ConnectionOpenedListener> connectionOpenedListeners;
    private List<ConnectionClosedListener> connectionClosedListeners;

    private void notifyConnectionOpenedListeners(Datastore datastore) {
        connectionOpenedListeners.forEach(listener -> listener.loaded(datastore));
    }

    public void addConnectionOpenedListener(ConnectionOpenedListener connectionOpenedListener) {
        connectionOpenedListeners.add(connectionOpenedListener);
    }

    public void removeConnectionOpenedListener(ConnectionOpenedListener connectionOpenedListener) {
        connectionOpenedListeners.remove(connectionOpenedListener);
    }

    private void notifyConnectionClosedListeners(Datastore datastore) {
        connectionClosedListeners.forEach(listener -> listener.closed(datastore));
    }

    public void addConnectionClosedListener(ConnectionClosedListener connectionClosedListener) {
        connectionClosedListeners.add(connectionClosedListener);
    }

    public void removeConnectionClosedListener(ConnectionClosedListener connectionClosedListener) {
        connectionClosedListeners.remove(connectionClosedListener);
    }

    public void init(String hostname, int port, String dbName, String username, String password, boolean useAuth) {

        String client_url;

        if (useAuth) {
            String encodedPassword = password;
            try {
                encodedPassword = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }

            client_url = "mongodb://" + username + ":" + encodedPassword + "@" + hostname + ":" + port + "/" + dbName;
        } else {
            client_url = "mongodb://" + hostname + ":" + port + "/" + dbName;
        }

        MongoClientURI uri = new MongoClientURI(client_url);

        MongoClient mongoClient = new MongoClient(uri);

        Morphia morphia = new Morphia();

        initMorphiaMaps(morphia);

        datastore = morphia.createDatastore(mongoClient, dbName);
        datastore.ensureIndexes();
        notifyConnectionOpenedListeners(datastore);
    }

    public void closeConnection() {
        if (datastore != null) {
            notifyConnectionClosedListeners(datastore);
            datastore.getMongo().close();
        }
    }

    public Optional<Datastore> getDataStore() {
        return Optional.ofNullable(datastore);
    }

    protected abstract void initMorphiaMaps(Morphia morphia);
}