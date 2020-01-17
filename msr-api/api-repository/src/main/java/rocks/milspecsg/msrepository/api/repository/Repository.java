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

import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends StorageService<TKey, T, TDataStore> {

    /**
     * @return The time of creation of this document in seconds since unix epoch
     */
    CompletableFuture<Optional<Integer>> getCreatedUtcTimeStampSeconds(TKey id);

    /**
     * @return The time of creation of this document in milliseconds since unix epoch
     */
    CompletableFuture<Optional<Long>> getCreatedUtcTimeStampMillis(TKey id);

    /**
     * @return The time of creation of this document as {@link Date}
     */
    CompletableFuture<Optional<Date>> getCreatedUtcDate(TKey id);
}
