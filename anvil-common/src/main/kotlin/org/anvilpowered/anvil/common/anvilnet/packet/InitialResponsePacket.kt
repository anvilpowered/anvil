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

import com.google.common.base.MoreObjects
import com.google.common.io.ByteArrayDataInput
import org.anvilpowered.anvil.common.anvilnet.communicator.NetworkHeader
import org.anvilpowered.anvil.common.anvilnet.network.BroadcastNetwork
import org.anvilpowered.anvil.common.anvilnet.network.PluginMessageNetwork
import org.anvilpowered.anvil.common.anvilnet.packet.data.BaseData
import org.anvilpowered.anvil.common.anvilnet.packet.data.NetworkTopographyData
import org.anvilpowered.anvil.common.anvilnet.packet.data.PlayerData
import java.util.UUID

class InitialResponsePacket : AnvilNetPacket {

  constructor(
    userUUID: UUID,
    broadcastNetwork: BroadcastNetwork,
    pluginMessageNetwork: PluginMessageNetwork,
  ) : super(
    BaseData(), PlayerData(userUUID), NetworkTopographyData(broadcastNetwork, pluginMessageNetwork)
  )

  constructor(header: NetworkHeader, input: ByteArrayDataInput) : super(
    header, BaseData(input), PlayerData(input), NetworkTopographyData(input),
  )

  val baseData: BaseData = getContainer(BaseData::class)!!
  val playerData: PlayerData = getContainer(PlayerData::class)!!
  val networkTopographyData: NetworkTopographyData = getContainer(NetworkTopographyData::class)!!

  // override to skip BaseData#updateNetwork as it is not needed
  override fun updateNetwork(broadcastNetwork: BroadcastNetwork, pluginMessageNetwork: PluginMessageNetwork) {
    networkTopographyData.updateNetwork(header!!, broadcastNetwork, pluginMessageNetwork)
  }

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("baseData", baseData)
      .add("playerData", playerData)
      .add("networkTopographyData", networkTopographyData)
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
