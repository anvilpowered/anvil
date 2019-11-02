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

package rocks.milspecsg.msrepository.datastore.mariadb;

import com.google.inject.Injector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public abstract class MariaContext extends DataStoreContext<UUID, HikariDataSource, DataStoreConfig> {

    protected MariaContext(DataStoreConfig config, Injector injector) {
        super(config, injector);
    }

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
//        notifyConnectionOpenedListeners(dataStore);
        setDataStore(dataStore);
    }

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