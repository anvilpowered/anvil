package rocks.milspecsg.msrepository.db.mongodb;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Singleton
public abstract class MongoContext {

    public final Datastore datastore;

    @Inject
    public MongoContext() {
//        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/",
//                MongoClientOptions.builder().sslEnabled(true));
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/");
        MongoClient mongoClient = new MongoClient(uri);

        Morphia morphia = new Morphia();

        initMorphiaMaps(morphia);

        datastore = morphia.createDatastore(mongoClient, getDbName());
        datastore.ensureIndexes();
    }

    protected abstract String getDbName();

    protected abstract void initMorphiaMaps(Morphia morphia);
}