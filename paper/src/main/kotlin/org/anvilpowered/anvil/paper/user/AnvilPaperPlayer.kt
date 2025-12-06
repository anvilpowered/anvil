/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.paper.user

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import java.util.UUID
import org.bukkit.entity.Player as PaperPlayer

fun PaperPlayer.toAnvilPlayer(): Player = AnvilPaperPlayer(this)

private class AnvilPaperPlayer(
  override val platformDelegate: PaperPlayer,
) : Player,
  ForwardingAudience,
  Subject by platformDelegate.toAnvilSubject() {
  val delegateAudiences = listOf<Audience>(platformDelegate)

  override fun audiences(): Iterable<Audience> = delegateAudiences

  override val id: UUID = platformDelegate.uniqueId
  override val username: String = platformDelegate.name
  override val displayname: Component = platformDelegate.displayName()
  override val latencyMs: Int
    get() = platformDelegate.ping
}
