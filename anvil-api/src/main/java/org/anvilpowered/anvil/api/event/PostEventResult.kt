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

package org.anvilpowered.anvil.api.event

import kotlin.reflect.KClass

/**
 * Recursive data structure with information about every [EventListener]
 */
data class PostEventResult(
  val batches: List<InvocationBatch>,
  val children: List<PostEventResult>,
) {

  /**
   * A batch of [Invocations][Invocation]. This represents all the listeners that run for a given
   * [Order]
   */
  data class InvocationBatch<E : Event>(val order: Order, val eventType: KClass<E>) {
    class Builder() {
      private val invocations = mutableListOf<Invocation>()

      fun invocationBuilder(): Invocation.Builder {
        return Invocation.Builder(invocations)
      }

      fun build(): InvocationBatch {

      }
    }
  }

  data class Invocation(val timeTaken: Long, val exception: Exception?) {
    class Builder(private val invocations: MutableList<Invocation>) {
      private var startTime: Long = 0
      private var timeTaken: Long = 0
      private var exception: Exception? = null

      fun startInvoke() {
        startTime = System.nanoTime()
      }

      fun endInvoke() {
        timeTaken = System.nanoTime() - startTime
      }

      fun endExceptionally(exception: Exception): Builder {
        this.exception = exception
        endInvoke()
        return this
      }

      fun finish() {
        invocations.add(Invocation(timeTaken, exception))
      }
    }
  }

  class Builder<E : Event> {
    private val invocations: MutableList<Invocation> = mutableListOf()
    private val children: MutableList<PostEventResult> = mutableListOf()



    fun child(other: PostEventResult) {
      children.add(other)
    }

    fun build(): PostEventResult {
      return PostEventResult(invocations, children)
    }
  }
}
