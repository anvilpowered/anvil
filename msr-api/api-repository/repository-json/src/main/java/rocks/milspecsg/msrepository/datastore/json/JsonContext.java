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

import io.jsondb.JsonDBTemplate;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;


public abstract class JsonContext extends DataStoreContext<JsonDBTemplate> {

    public void init(String baseScanPackage, int port, String dbFilesLocation, String username, String password, boolean useAuth) {
        if (!getDataStore().isPresent()) {
            JsonDBTemplate dataStore = new JsonDBTemplate(dbFilesLocation, baseScanPackage);
            initCollections(dataStore);
            setDataStore(dataStore);
        }
    }

    @Override
    protected void closeConnection(JsonDBTemplate dataStore) {
    }

    protected abstract void initCollections(JsonDBTemplate dataStore);
}
