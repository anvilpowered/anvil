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
package org.anvilpowered.anvil.common.anvilnet.packet

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.data.DataContainer
import org.anvilpowered.anvil.common.anvilnet.packet.data.NetworkData
import org.anvilpowered.anvil.common.anvilnet.packet.data.Preparable
import kotlin.reflect.KClass

open class AnvilNetPacket(
  val header: NetworkHeader?,
  vararg containerInstances: DataContainer,
) : DataContainer, Preparable {

  val containers: Map<KClass<out DataContainer>, DataContainer> =
    containerInstances.asSequence().map { it::class to it }.toMap()

  constructor(vararg containerInstances: DataContainer) : this(null, *containerInstances)

  inline fun <reified T : DataContainer> getContainer(type: KClass<T>): T? = containers[type] as? T

  /**
   * Updates provided networks with information from this packet.
   * Precondition: header != null
   */
  open fun updateNetwork(
    broadcastNetwork: BroadcastNetwork,
    pluginMessageNetwork: PluginMessageNetwork,
  ) {
    requireNotNull(header) { "header is null, updateNetwork must be called on a received packet" }
    containers.forEach { (it.value as? NetworkData)?.updateNetwork(header, broadcastNetwork, pluginMessageNetwork) }
  }

  override fun prepare(nodeName: String) = containers.forEach { (it.value as? Preparable)?.prepare(nodeName) }
  override fun write(output: ByteArrayDataOutput) = containers.forEach { it.value.write(output) }
  override fun read(input: ByteArrayDataInput) = containers.forEach { it.value.read(input) }
}
