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
