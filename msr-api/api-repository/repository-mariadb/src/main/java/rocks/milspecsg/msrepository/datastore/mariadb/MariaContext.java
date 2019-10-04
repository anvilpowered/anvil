package rocks.milspecsg.msrepository.datastore.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public abstract class MariaContext extends DataStoreContext<HikariDataSource> {

    public void init(String hostname, int port, String dbName, String username, String password, boolean useAuth) {

        HikariConfig config = new HikariConfig();
        if (useAuth) {
            String encodedPassword = password;
            try {
                encodedPassword = URLEncoder.encode(password, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {
            }
            config.setPassword(encodedPassword);
        }
        config.setJdbcUrl("jdbc:mariadb://" + hostname + ":" + port);
        config.setUsername(username);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setMaximumPoolSize(20);
        HikariDataSource dataStore = new HikariDataSource(config);
        notifyConnectionOpenedListeners(dataStore);
        setDataStore(dataStore);
    }

    @Override
    protected void closeConnection(HikariDataSource dataStore) {
        dataStore.close();
    }

    public Optional<Connection> getConnection()  {
        Optional<HikariDataSource> ds = getDataStore();
        if (ds.isPresent()) {
            try {
                return Optional.ofNullable(ds.get().getConnection());
            } catch (SQLException ignored) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}