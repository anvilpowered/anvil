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

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import rocks.milspecsg.msrepository.api.component.Component;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;

import java.util.Optional;

public interface CommonNitriteComponent extends Component<NitriteId, Nitrite, NitriteConfig> {

    @Override
    default NitriteId parseUnsafe(Object object) {
        if (object instanceof NitriteId) {
            return (NitriteId) object;
        } else if (object instanceof Optional<?>) {
            Optional<?> optional = (Optional<?>) object;
            if (optional.isPresent()) return parseUnsafe(optional.get());
            throw new IllegalArgumentException("Error while parsing " + object + ". Optional not present");
        } else if (object instanceof Long) {
            return NitriteId.createId((Long) object);
        }
        String string = object.toString();
        long value;
        try {
            value = Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error while parsing " + object + ". Not a valid NitriteId", e);
        }
        return NitriteId.createId(value);
    }
}
