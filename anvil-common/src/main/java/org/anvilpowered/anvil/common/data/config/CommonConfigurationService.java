/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.common.data.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.base.data.config.BaseConfigurationService;

@Singleton
public class CommonConfigurationService extends BaseConfigurationService {

    @Inject
    public CommonConfigurationService(
        ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }

    @Override
    protected void initNodeNameMap() {
        setName(Keys.SERVER_NAME, "server.name");
        setName(Keys.PROXY_MODE, "server.proxyMode");
        setName(Keys.DATA_DIRECTORY, "datastore.dataDirectory");
        setName(Keys.DATA_STORE_NAME, "datastore.dataStoreName");
        setName(Keys.MONGODB_CONNECTION_STRING, "datastore.mongodb.connectionString");
        setName(Keys.MONGODB_HOSTNAME, "datastore.mongodb.hostname");
        setName(Keys.MONGODB_PORT, "datastore.mongodb.port");
        setName(Keys.MONGODB_DBNAME, "datastore.mongodb.dbname");
        setName(Keys.MONGODB_USERNAME, "datastore.mongodb.username");
        setName(Keys.MONGODB_PASSWORD, "datastore.mongodb.password");
        setName(Keys.MONGODB_AUTH_DB, "datastore.mongodb.authDb");
        setName(Keys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth");
        setName(Keys.MONGODB_USE_SRV, "datastore.mongodb.useSrv");
        setName(Keys.MONGODB_USE_CONNECTION_STRING, "datastore.mongodb.useConnectionString");
    }

    @Override
    protected void initNodeDescriptionMap() {
        setDescription(Keys.SERVER_NAME, "\nServer name");
        setDescription(Keys.PROXY_MODE, "\nEnable this if your server is running behind a proxy"
            + "\nIf true, this setting disables the join and chat listeners"
            + "\nto prevent conflicts with the proxy's listeners.");
        setDescription(Keys.DATA_DIRECTORY, "\nDirectory for extra data" +
            "\nPlease note that it is not recommended to change this value from the original");
        setDescription(Keys.DATA_STORE_NAME, "\nDetermines which storage option to use");
        setDescription(Keys.MONGODB_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nCustom MongoDB connection string that will used instead of the connection info and credentials below" +
            "\nWill only be used if useConnectionString=true");
        setDescription(Keys.MONGODB_HOSTNAME, "\nMongoDB hostname");
        setDescription(Keys.MONGODB_PORT, "\nMongoDB port");
        setDescription(Keys.MONGODB_DBNAME, "\nMongoDB database name");
        setDescription(Keys.MONGODB_USERNAME, "\nMongoDB username");
        setDescription(Keys.MONGODB_PASSWORD, "\nMongoDB password");
        setDescription(Keys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication");
        setDescription(Keys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
        setDescription(Keys.MONGODB_USE_SRV, "\nWhether to interpret the MongoDB hostname as an SRV record");
        setDescription(Keys.MONGODB_USE_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nWhether to use the connection string provided instead of the normal connection info and credentials" +
            "\nOnly use this if you know what you are doing!" +
            "\nPlease note that plugins using Anvil will inherit both useConnectionString and connectionString from" +
            "\nAnvil if and only if useSharedEnvironment and useSharedCredentials are both true");
    }
}
