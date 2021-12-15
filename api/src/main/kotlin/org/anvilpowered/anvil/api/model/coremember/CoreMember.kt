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
package org.anvilpowered.anvil.api.model.coremember

import java.util.UUID
import java.time.Instant
import org.anvilpowered.anvil.api.model.ObjectWithId

interface CoreMember<TKey> : ObjectWithId<TKey> {
  var userUUID: UUID?
  var userName: String?
  var ipAddress: String?
  var lastJoinedUtc: Instant?
  var nickName: String?
  var isBanned: Boolean
  var isMuted: Boolean
  var banEndUtc: Instant?
  var muteEndUtc: Instant?
  var banReason: String?
  var muteReason: String?
}
