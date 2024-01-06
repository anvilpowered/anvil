/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
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

package org.anvilpowered.anvil.paper.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.anvilpowered.anvil.paper.user.toAnvilPlayer
import org.anvilpowered.anvil.paper.user.toAnvilSubject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player as PaperPlayer

fun CommandSender.toAnvilCommandSource(): CommandSource = AnvilPaperCommandSource(this)

@Suppress("UnstableApiUsage")
fun CommandSourceStack.toAnvilCommandSource(): CommandSource = AnvilPaperCommandSource(sender)

private class AnvilPaperCommandSource(
    override val platformDelegate: CommandSender,
) : CommandSource,
    ForwardingAudience,
    Subject by platformDelegate.toAnvilSubject() {

    val delegateAudiences = listOf<Audience>(platformDelegate)
    override fun audiences(): Iterable<Audience> = delegateAudiences

    override val player: Player? = (platformDelegate as? PaperPlayer)?.toAnvilPlayer()
}
