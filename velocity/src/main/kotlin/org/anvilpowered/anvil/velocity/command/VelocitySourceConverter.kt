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

@file:JvmName("VelocitySourceConverter")

package org.anvilpowered.anvil.velocity.command

import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.kbrig.brigadier.toBrigadier
import org.anvilpowered.kbrig.tree.ArgumentCommandNode
import org.anvilpowered.kbrig.tree.LiteralCommandNode
import org.anvilpowered.kbrig.tree.mapSource
import com.mojang.brigadier.tree.ArgumentCommandNode as BrigadierArgumentCommandNode
import com.mojang.brigadier.tree.LiteralCommandNode as BrigadierLiteralCommandNode1
import com.velocitypowered.api.command.CommandSource as VelocityCommandSource

/**
 * Converts a kbrig argument command node to a velocity brigadier argument command node.
 */
fun <T> ArgumentCommandNode<CommandSource, T>.toVelocity(): BrigadierArgumentCommandNode<VelocityCommandSource, T> =
    mapSource<CommandSource, VelocityCommandSource, T> { it.toAnvilCommandSource() }.toBrigadier()

/**
 * Converts a kbrig literal command node to a velocity brigadier literal command node.
 */
fun LiteralCommandNode<CommandSource>.toVelocity(): BrigadierLiteralCommandNode1<VelocityCommandSource> =
    mapSource<CommandSource, VelocityCommandSource> { it.toAnvilCommandSource() }.toBrigadier()
