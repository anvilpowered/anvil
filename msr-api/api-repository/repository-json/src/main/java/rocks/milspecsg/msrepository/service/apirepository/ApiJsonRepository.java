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

package rocks.milspecsg.msrepository.service.apirepository;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.api.repository.JsonRepository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

public abstract class ApiJsonRepository<T extends ObjectWithId<ObjectId>> extends ApiRepository<ObjectId, T, Object> implements JsonRepository<T> {

    public ApiJsonRepository(DataStoreContext<Object> dataStoreContext) {
        super(dataStoreContext);
    }
}
