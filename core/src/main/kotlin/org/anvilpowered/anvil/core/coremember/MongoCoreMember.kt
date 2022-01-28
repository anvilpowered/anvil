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
package org.anvilpowered.anvil.core.coremember

import dev.morphia.annotations.Entity
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.base.model.MongoDbo
import org.bson.types.ObjectId
import java.time.Instant
import java.util.UUID

@Entity("coreMembers")
class MongoCoreMember : MongoDbo(), CoreMember<ObjectId> {
    override lateinit var userUUID: UUID
    override lateinit var userName: String
    override lateinit var ipAddress: String
    override lateinit var lastJoinedUtc: Instant
    override lateinit var nickName: String
    override var isBanned = false
    override var isMuted = false
    override lateinit var banEndUtc: Instant
    override lateinit var muteEndUtc: Instant
    override lateinit var banReason: String
    override lateinit var muteReason: String
}