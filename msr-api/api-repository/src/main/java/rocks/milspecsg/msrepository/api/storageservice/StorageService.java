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

package rocks.milspecsg.msrepository.api.storageservice;

import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface StorageService<T> {

    /**
     * @return An empty {@link T}
     */
    T generateEmpty();

    TypeToken<T> getTypeTokenT();

    /**
     * @param item Object to insert
     * @return The inserted item
     */
    CompletableFuture<Optional<T>> insertOne(T item);

    /**
     * @param list {@link List <T>} to insert
     * @return {@link CompletableFuture} of {@link List<T>} containing all {@link T} that were successfully inserted
     */
    CompletableFuture<List<T>> insert(List<T> list);

}
