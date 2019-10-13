/*
 *     MSParties - MilSpecSG
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

package rocks.milspecsg.msrepository.datastore;


import com.google.inject.Injector;

public class EmptyContext extends DataStoreContext<Object, Object, DataStoreConfig> {

    public EmptyContext(DataStoreConfig config, Injector injector) {
        super(config, injector);
    }

    @Override
    public void configLoaded(Object plugin) {
    }

    @Override
    protected void closeConnection(Object o) {
    }

}
