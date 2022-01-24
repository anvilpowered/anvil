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
import org.anvilpowered.anvil.api.model.ObjectWithId
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.slf4j.Logger
import java.util.concurrent.CompletableFuture

abstract class BaseRepository<TKey, T : ObjectWithId<TKey>, TDataStore> : BaseComponent<TKey, TDataStore>(),
    Repository<TKey, T, TDataStore> {

    @Inject
    private lateinit var logger: Logger

    override fun generateEmpty(): T {
        val tClass = tClass
        return try {
            tClass.getConstructor().newInstance()
        } catch (e: Exception) {
            val message =
                "There was an error creating an instance of " + tClass.name + "! Make sure it has an accessible no-args constructor!"
            logger.error(message)
            throw IllegalStateException(message, e)
        }
    }


    override fun parseAndGetOne(idOrTime: Any): CompletableFuture<T?> {
        parse(idOrTime).also {
            if (it == null) {
                Anvil.getEnvironmentManager().coreEnvironment.injector.getInstance(TimeFormatService::class.java)
                    .parseInstant(idOrTime.toString()).also { time ->
                        if (time == null) {
                            return CompletableFuture.completedFuture(null)
                        }
                        return getOne(time)
                    }
            }
            return getOne(it!!)
        }
    }

    override fun parseAndDeleteOne(idOrTime: Any): CompletableFuture<Boolean> {
        parse(idOrTime).also {
            if (it == null) {
                Anvil.getEnvironmentManager().coreEnvironment.injector.getInstance(TimeFormatService::class.java)
                    .parseInstant(idOrTime.toString()).also { time ->
                        if (time == null) {
                            return CompletableFuture.completedFuture(false)
                        }
                        return deleteOne(time)
                    }
            }
            return deleteOne(it!!)
        }
    }
}
