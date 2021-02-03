/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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
package org.anvilpowered.anvil.common.event

import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.EventListener
import java.lang.reflect.Method
import java.util.function.BiFunction
import java.util.function.Supplier

class MethodEventListener<E : Event?>(
    private val method: Method, private val parser: BiFunction<Method?, E, Array<Any>>,
    private val instanceSupplier: Supplier<*>
) : EventListener<E> {

    @Throws(Exception::class)
    override fun handle(event: E) {
        method.invoke(instanceSupplier.get(), *parser.apply(method, event))
    }
}
