package rocks.milspecsg.msrepository.datastore.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class MongoContext extends DataStoreContext<Datastore> {

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
        Datastore dataStore = morphia.createDatastore(mongoClient, dbName);
        dataStore.ensureIndexes();
        notifyConnectionOpenedListeners(dataStore);
        setDataStore(dataStore);
    }

    @Override
    protected void closeConnection(Datastore datastore) {
        datastore.getMongo().close();
    }

    protected abstract void initMorphiaMaps(Morphia morphia);
}