/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
package org.anvilpowered.anvil.base.datastore

import com.google.inject.Inject
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.datastore.Repository
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.slf4j.Logger
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import java.util.function.Supplier

abstract class BaseRepository<TKey, T : ObjectWithId<TKey>?, TDataStore> : BaseComponent<TKey, TDataStore>(),
    Repository<TKey, T, TDataStore> {
    @Inject
    private val logger: Logger? = null
    override fun generateEmpty(): T {
        val tClass = tClass!!
        return try {
            tClass.getConstructor().newInstance()
        } catch (e: Exception) {
            val message =
                "There was an error creating an instance of " + tClass.name + "! Make sure it has an accessible no-args constructor!"
            logger!!.error(message)
            throw IllegalStateException(message, e)
        }
    }

    override fun parseAndGetOne(idOrTime: Any?): CompletableFuture<Optional<T>?>? {
        return parse(idOrTime).map<CompletableFuture<Optional<T>>>(Function<TKey, CompletableFuture<Optional<T?>>> { id: TKey ->
            this.getOne(id)
        }).orElseGet(
            Supplier<CompletableFuture<Optional<T>>> {
                Anvil.getEnvironmentManager().getCoreEnvironment().getInjector()
                    .getInstance(TimeFormatService::class.java).parseInstant(idOrTime.toString())
                    .map(this::getOne).orElse(CompletableFuture.completedFuture(Optional.empty<Any>()))
            })
    }

    override fun parseAndDeleteOne(idOrTime: Any?): CompletableFuture<Boolean?>? {
        return parse(idOrTime).map<CompletableFuture<Boolean>>(Function<TKey, CompletableFuture<Boolean?>?> { id: TKey ->
            this.deleteOne(id)
        }).orElseGet(
            Supplier<CompletableFuture<Boolean>> {
                Anvil.getEnvironmentManager().getCoreEnvironment().getInjector()
                    .getInstance(TimeFormatService::class.java).parseInstant(idOrTime.toString())
                    .map(this::deleteOne).orElse(CompletableFuture.completedFuture(false))
            })
    }
}
