package rocks.milspecsg.msrepository.db.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class MongoContext {

    public Datastore datastore;

    public void init(String hostname, int port, String dbName, String user, String password) {
        String encodedPassword = password;
        try {
            encodedPassword = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }

        String client_url = "mongodb://" + user + ":" + encodedPassword + "@" + hostname + ":" + port + "/";
        MongoClientURI uri = new MongoClientURI(client_url);

        MongoClient mongoClient = new MongoClient(uri);

        Morphia morphia = new Morphia();

        initMorphiaMaps(morphia);

        datastore = morphia.createDatastore(mongoClient, dbName);
        datastore.ensureIndexes();
    }

    protected abstract void initMorphiaMaps(Morphia morphia);
}