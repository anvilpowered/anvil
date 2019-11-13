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

package rocks.milspecsg.msrepository.service.common.component;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import rocks.milspecsg.msrepository.api.component.Component;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoConfig;

import java.util.Optional;

public interface CommonMongoComponent extends Component<ObjectId, Datastore, MongoConfig> {

    @Override
    default Optional<ObjectId> parse(Object object) {
        if (object instanceof ObjectId) {
            return Optional.of((ObjectId) object);
        } else if (object instanceof Optional<?>) {
            Optional<?> optional = (Optional<?>) object;
            return optional.isPresent() ? parse(optional.get()) : Optional.empty();
        }
        String string = object.toString();
        return ObjectId.isValid(string) ? Optional.of(new ObjectId(string)) : Optional.empty();
    }
}
