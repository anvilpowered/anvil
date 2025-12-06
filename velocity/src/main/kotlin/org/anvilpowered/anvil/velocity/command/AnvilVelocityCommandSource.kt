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

package org.anvilpowered.anvil.velocity.command

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.anvilpowered.anvil.velocity.user.toAnvilPlayer
import org.anvilpowered.anvil.velocity.user.toAnvilSubject
import com.velocitypowered.api.command.CommandSource as VelocityCommandSource
import com.velocitypowered.api.proxy.Player as VelocityPlayer

fun VelocityCommandSource.toAnvilCommandSource(): CommandSource = AnvilVelocityCommandSource(this)

private class AnvilVelocityCommandSource(
  override val platformDelegate: VelocityCommandSource,
) : CommandSource,
  ForwardingAudience,
  Subject by platformDelegate.toAnvilSubject() {
  val delegateAudiences = listOf<Audience>(platformDelegate)

  override fun audiences(): Iterable<Audience> = delegateAudiences

  override val player: Player? = (platformDelegate as? VelocityPlayer)?.toAnvilPlayer()
}
