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

package rocks.milspecsg.msrepository.api.repository;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface NitriteRepository<
    T extends ObjectWithId<NitriteId>,
    C extends CacheService<NitriteId, T, Nitrite, NitriteConfig>>
    extends Repository<NitriteId, T, C, Nitrite, NitriteConfig> {

    CompletableFuture<WriteResult> delete(ObjectFilter filter);

    ObjectFilter asFilter(NitriteId id);

    Optional<Cursor<T>> asCursor(ObjectFilter filter);

    Optional<Cursor<T>> asCursor(NitriteId id);
}
