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

package org.anvilpowered.anvil.common.event

import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.EventResult
import org.anvilpowered.anvil.api.event.Order
import java.time.Duration
import java.util.Optional
import kotlin.reflect.KClass

class EventResultImpl<E : Event>(
  private val eventType: KClass<E>,
) : EventResult<E> {
  private val eventTypeJava = eventType.java
  private val batches: MutableList<Batch<E>> = mutableListOf()

  override fun getEventType(): Class<E> = eventTypeJava

  fun addBatch(batch: Batch<E>) {
    batches.add(batch)
    batch.parentResult = this
  }

  override fun getBatches(): List<Batch<E>> = batches

  class Batch<E : Event>(
    private val order: Order,
  ) : EventResult.Batch<E> {
    private lateinit var parentResult: EventResultImpl<E>
    private val trees: MutableList<Tree<E>> = mutableListOf()

    fun setParentResult(parentResult: EventResultImpl<E>) {
      this.parentResult = parentResult
    }

    fun addTree(tree: Tree<E>) {
      synchronized(this) {
        trees.add(tree)
        tree.parentBatch = this
      }
    }

    override fun getParentResult(): EventResultImpl<E> = parentResult
    override fun getOrder(): Order = order
    override fun getTrees(): List<Tree<E>> = trees
  }

  class Tree<E : Event>(
    private val eventType: KClass<E>,
  ) : EventResult.Tree<E> {
    private val eventTypeJava = eventType.java
    private lateinit var parentBatch: Batch<out E>
    private var parentTree: Tree<out E>? = null
    private val children: MutableList<Tree<in E>> = mutableListOf()
    private val invocations: MutableList<Invocation<E>> = mutableListOf()

    fun setParentBatch(parentBatch: Batch<out E>) {
      this.parentBatch = parentBatch
    }

    fun addChild(tree: Tree<in E>) {
      children.add(tree)
      tree.parentTree = this
    }

    fun addInvocation(invocation: Invocation<E>) {
      invocations.add(invocation)
      invocation.parentTree = this
    }

    override fun getEventType(): Class<E> = eventTypeJava
    override fun getParentBatch(): Batch<out E> = parentBatch
    override fun getParent(): Optional<Tree<out E>> = Optional.ofNullable(parentTree)
    override fun getChildren(): List<Tree<in E>> = children
    override fun getInvocations(): List<Invocation<E>> = invocations
  }

  class Invocation<E : Event> : EventResult.Invocation<E> {
    private lateinit var parentTree: Tree<E>
    private var startTime: Long = 0
    private lateinit var duration: Duration
    private var exception: Exception? = null

    fun startInvoke() {
      startTime = System.nanoTime()
    }

    fun endInvoke() {
      duration = Duration.ofNanos(System.nanoTime() - startTime)
    }

    fun endExceptionally(exception: Exception) {
      this.exception = exception
      endInvoke()
    }

    fun setParentTree(tree: Tree<E>) {
      this.parentTree = tree
    }

    override fun getParentTree(): Tree<E> = parentTree
    override fun getDuration(): Duration = duration
    override fun getException(): Optional<out Exception> = Optional.ofNullable(exception)
  }
}
