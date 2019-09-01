package rocks.milspecsg.msrepository.db.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

public abstract class MongoContext {

    private Datastore datastore = null;

    public void init(String hostname, int port, String dbName, String username, String password, boolean useAuth) {

        String client_url;

        if (useAuth) {
            String encodedPassword = password;
            try {
                encodedPassword = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }

            client_url = "mongodb://" + username + ":" + encodedPassword + "@" + hostname + ":" + port + "/";
        } else {
            client_url = "mongodb://" + hostname + ":" + port + "/";
        }

        MongoClientURI uri = new MongoClientURI(client_url);

        MongoClient mongoClient = new MongoClient(uri);

        Morphia morphia = new Morphia();

        initMorphiaMaps(morphia);

        datastore = morphia.createDatastore(mongoClient, dbName);
        datastore.ensureIndexes();
    }

    public void closeConnection() {
        if (datastore != null) datastore.getMongo().close();
    }

    public Optional<Datastore> getDataStore() {
        return Optional.ofNullable(datastore);
    }

    protected abstract void initMorphiaMaps(Morphia morphia);
}