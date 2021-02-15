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

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.api.event.Event
import org.anvilpowered.anvil.api.event.EventPostResult
import org.anvilpowered.anvil.api.event.Order
import org.anvilpowered.anvil.common.anvilnet.packet.data.DataContainer
import org.anvilpowered.anvil.common.anvilnet.packet.data.read
import org.anvilpowered.anvil.common.anvilnet.packet.data.readOrder
import org.anvilpowered.anvil.common.anvilnet.packet.data.readShortContainersAsList
import org.anvilpowered.anvil.common.anvilnet.packet.data.write
import org.anvilpowered.anvil.common.anvilnet.packet.data.writeOrder
import org.anvilpowered.anvil.common.anvilnet.packet.data.writeShortContainers
import java.time.Duration
import java.util.Optional
import kotlin.reflect.KClass

class EventPostResultImpl<E : Event> : EventPostResult<E>, DataContainer {

  private lateinit var eventType: KClass<E>
  private lateinit var eventTypeJava: Class<E>
  private lateinit var batches: MutableList<Batch<E>>

  constructor(eventType: KClass<E>) {
    this.eventType = eventType
    this.eventTypeJava = eventType.java
    this.batches = mutableListOf()
  }

  constructor(input: ByteArrayDataInput) {
    read(input)
  }

  override fun read(input: ByteArrayDataInput) {
    eventType = input.read()!!
    eventTypeJava = eventType.java
    batches = input.readShortContainersAsList()
    batches.forEach { it.parentResult = this }
  }

  override fun write(output: ByteArrayDataOutput) {
    output.write(eventType)
    output.writeShortContainers(batches)
  }

  fun addBatch(batch: Batch<E>) {
    batches.add(batch)
    batch.parentResult = this
  }

  override fun getEventType(): Class<E> = eventTypeJava
  override fun getBatches(): List<Batch<E>> = batches

  class Batch<E : Event> : EventPostResult.Batch<E>, DataContainer {

    private lateinit var order: Order
    private lateinit var trees: MutableList<Tree<E>>

    @Transient
    private lateinit var parentResult: EventPostResultImpl<E>

    constructor(order: Order) {
      this.order = order
      this.trees = mutableListOf()
    }

    constructor(input: ByteArrayDataInput) {
      read(input)
    }

    override fun read(input: ByteArrayDataInput) {
      order = input.readOrder()
      trees = input.readShortContainersAsList()
      trees.forEach { it.parentBatch = this }
    }

    override fun write(output: ByteArrayDataOutput) {
      output.writeOrder(order)
      output.writeShortContainers(trees)
    }

    fun setParentResult(parentResult: EventPostResultImpl<E>) {
      this.parentResult = parentResult
    }

    fun addTree(tree: Tree<E>) {
      synchronized(this) {
        trees.add(tree)
        tree.parentBatch = this
      }
    }

    override fun getParentResult(): EventPostResultImpl<E> = parentResult
    override fun getOrder(): Order = order
    override fun getTrees(): List<Tree<E>> = trees
  }

  class Tree<E : Event> : EventPostResult.Tree<E>, DataContainer {

    private lateinit var eventType: KClass<E>
    private lateinit var eventTypeJava: Class<E>
    private lateinit var invocations: MutableList<Invocation<E>>
    private lateinit var children: MutableList<Tree<in E>>

    @Transient
    private lateinit var parentBatch: Batch<out E>

    @Transient
    private var parentTree: Tree<out E>? = null

    constructor(eventType: KClass<E>) {
      this.eventType = eventType
      this.eventTypeJava = eventType.java
      this.invocations = mutableListOf()
      this.children = mutableListOf()
    }

    constructor(input: ByteArrayDataInput) {
      read(input)
    }

    override fun read(input: ByteArrayDataInput) {
      eventType = input.read()!!
      eventTypeJava = eventType.java
      invocations = input.readShortContainersAsList()
      invocations.forEach { it.parentTree = this }
      children = input.readShortContainersAsList()
      children.forEach { it.parentTree = this }
    }

    override fun write(output: ByteArrayDataOutput) {
      output.write(eventType)
      output.writeShortContainers(invocations)
      output.writeShortContainers(children)
    }

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
    override fun getInvocations(): List<Invocation<E>> = invocations
    override fun getParentBatch(): Batch<out E> = parentBatch
    override fun getParent(): Optional<Tree<out E>> = Optional.ofNullable(parentTree)
    override fun getChildren(): List<Tree<in E>> = children
  }

  class Invocation<E : Event> : EventPostResult.Invocation<E>, DataContainer {
    private lateinit var duration: Duration
    private var exception: Exception? = null

    @Transient
    private var startTime: Long = 0

    @Transient
    private lateinit var parentTree: Tree<E>

    constructor() {
    }

    constructor(input: ByteArrayDataInput) {
      read(input)
    }

    override fun read(input: ByteArrayDataInput) {
      duration = Duration.ofNanos(input.readLong())
      exception = input.read()
    }

    override fun write(output: ByteArrayDataOutput) {
      output.write(duration.toNanos())
      output.write(exception)
    }

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
