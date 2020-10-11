/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.base.datastore;

import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.datastore.Repository;
import org.anvilpowered.anvil.api.model.ObjectWithId;
import org.anvilpowered.anvil.api.util.TimeFormatService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends BaseComponent<TKey, TDataStore>
    implements Repository<TKey, T, TDataStore> {

    @Override
    public T generateEmpty() {
        Class<T> tClass = getTClass();
        try {
            return tClass.getConstructor().newInstance();
        } catch (Exception e) {
            String message = "There was an error creating an instance of " + tClass.getName() + "! Make sure it has an accessible no-args constructor!";
            System.err.println(message);
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    public CompletableFuture<Optional<T>> parseAndGetOne(Object idOrTime) {
        return parse(idOrTime).map(this::getOne).orElseGet(() ->
            Anvil.getEnvironmentManager().getCoreEnvironment().getInjector()
                .getInstance(TimeFormatService.class).parseInstant(idOrTime.toString())
                .map(this::getOne).orElse(CompletableFuture.completedFuture(Optional.empty())));
    }

    @Override
    public CompletableFuture<Boolean> parseAndDeleteOne(Object idOrTime) {
        return parse(idOrTime).map(this::deleteOne).orElseGet(() ->
            Anvil.getEnvironmentManager().getCoreEnvironment().getInjector()
                .getInstance(TimeFormatService.class).parseInstant(idOrTime.toString())
                .map(this::deleteOne).orElse(CompletableFuture.completedFuture(false)));
    }
}
