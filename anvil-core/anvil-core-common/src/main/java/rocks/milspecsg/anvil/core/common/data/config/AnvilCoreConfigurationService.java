/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.core.common.data.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import rocks.milspecsg.anvil.api.data.key.Keys;
import rocks.milspecsg.anvil.common.data.config.CommonConfigurationService;

@Singleton
public class AnvilCoreConfigurationService extends CommonConfigurationService {

    @Inject
    public AnvilCoreConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }

    @Override
    protected void initNodeNameMap() {
        nodeNameMap.put(Keys.DATA_DIRECTORY, "datastore.dataDirectory");
        nodeNameMap.put(Keys.DATA_STORE_NAME, "datastore.dataStoreName");
        nodeNameMap.put(Keys.MONGODB_CONNECTION_STRING, "datastore.mongodb.connectionString");
        nodeNameMap.put(Keys.MONGODB_HOSTNAME, "datastore.mongodb.hostname");
        nodeNameMap.put(Keys.MONGODB_PORT, "datastore.mongodb.port");
        nodeNameMap.put(Keys.MONGODB_DBNAME, "datastore.mongodb.dbname");
        nodeNameMap.put(Keys.MONGODB_USERNAME, "datastore.mongodb.username");
        nodeNameMap.put(Keys.MONGODB_PASSWORD, "datastore.mongodb.password");
        nodeNameMap.put(Keys.MONGODB_AUTH_DB, "datastore.mongodb.authDb");
        nodeNameMap.put(Keys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth");
        nodeNameMap.put(Keys.MONGODB_USE_SRV, "datastore.mongodb.useSrv");
        nodeNameMap.put(Keys.MONGODB_USE_CONNECTION_STRING, "datastore.mongodb.useConnectionString");
    }

    @Override
    protected void initNodeDescriptionMap() {
        nodeDescriptionMap.put(Keys.DATA_DIRECTORY, "\nDirectory for extra data" +
            "\nPlease note that it is not recommended to change this value from the original");
        nodeDescriptionMap.put(Keys.DATA_STORE_NAME, "\nDetermines which storage option to use");
        nodeDescriptionMap.put(Keys.MONGODB_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nCustom MongoDB connection string that will used instead of the connection info and credentials below" +
            "\nWill only be used if useConnectionString=true");
        nodeDescriptionMap.put(Keys.MONGODB_HOSTNAME, "\nMongoDB hostname");
        nodeDescriptionMap.put(Keys.MONGODB_PORT, "\nMongoDB port");
        nodeDescriptionMap.put(Keys.MONGODB_DBNAME, "\nMongoDB database name");
        nodeDescriptionMap.put(Keys.MONGODB_USERNAME, "\nMongoDB username");
        nodeDescriptionMap.put(Keys.MONGODB_PASSWORD, "\nMongoDB password");
        nodeDescriptionMap.put(Keys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication");
        nodeDescriptionMap.put(Keys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
        nodeDescriptionMap.put(Keys.MONGODB_USE_SRV, "\nWhether to interpret the MongoDB hostname as an SRV record");
        nodeDescriptionMap.put(Keys.MONGODB_USE_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nWhether to use the connection string provided instead of the normal connection info and credentials" +
            "\nOnly use this if you know what you are doing!" +
            "\nPlease note that plugins using Anvil will inherit both useConnectionString and connectionString from" +
            "\nAnvil if and only if useSharedEnvironment and useSharedCredentials are both true");
    }
}
