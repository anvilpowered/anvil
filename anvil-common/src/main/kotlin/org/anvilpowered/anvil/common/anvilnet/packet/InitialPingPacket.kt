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
import org.anvilpowered.anvil.common.anvilnet.packet.data.BaseData
import org.anvilpowered.anvil.common.anvilnet.packet.data.PlayerData
import java.util.UUID

class InitialPingPacket : AnvilNetPacket {

  constructor(userUUID: UUID) : super(BaseData(), PlayerData(userUUID))

  constructor(header: NetworkHeader, input: ByteArrayDataInput) : super(header, BaseData(input), PlayerData(input))

  val baseData: BaseData = getContainer(BaseData::class)!!
  val playerData: PlayerData = getContainer(PlayerData::class)!!

  private val stringRepresentation: String by lazy {
    MoreObjects.toStringHelper(this)
      .add("baseData", this.baseData)
      .add("playerData", this.playerData)
      .toString()
  }

  override fun toString(): String = stringRepresentation
}
