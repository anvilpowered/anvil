/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api.datastore;

import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public final class SqlContext extends DataStoreContext<UUID, HikariDataSource> {

    @Inject
    public SqlContext(Registry registry) {
        super(registry);
    }

    @Override
    public HikariDataSource loadDataStore() {

        String hostname = Anvil.resolveForSharedEnvironment(Keys.resolveUnsafe("MARIADB_HOSTNAME"), registry);
        int port = Anvil.resolveForSharedEnvironment(Keys.resolveUnsafe("MARIADB_PORT"), registry);
        String username = Anvil.resolveForSharedEnvironment(Keys.resolveUnsafe("MARIADB_USERNAME"), registry);
        String password = Anvil.resolveForSharedEnvironment(Keys.resolveUnsafe("MARIADB_PASSWORD"), registry);
        boolean useAuth = Anvil.resolveForSharedEnvironment(Keys.resolveUnsafe("MARIADB_USE_AUTH"), registry);

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
        return new HikariDataSource(config);
    }

    protected void closeConnection(HikariDataSource dataStore) {
        dataStore.close();
    }
}