package rocks.milspecsg.msrepository.datastore.json;

import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class JsonContext extends DataStoreContext<JsonDataStore> {

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

        JsonDataStore dataStore = new JsonDataStore();
        setDataStore(dataStore);
    }

    @Override
    protected void closeConnection(JsonDataStore dataStore) {

    }
}
