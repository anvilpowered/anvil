/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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

@file:JvmName("PaperSourceConverter")
@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.command

import io.papermc.paper.command.brigadier.CommandBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.kbrig.brigadier.toBrigadier
import org.anvilpowered.kbrig.tree.ArgumentCommandNode
import org.anvilpowered.kbrig.tree.LiteralCommandNode
import org.anvilpowered.kbrig.tree.mapSource
import org.bukkit.plugin.Plugin
import com.mojang.brigadier.tree.ArgumentCommandNode as BrigadierArgumentCommandNode
import com.mojang.brigadier.tree.LiteralCommandNode as BrigadierLiteralCommandNode1

/**
 * Converts a kbrig argument command node to a paper brigadier argument command node.
 */
fun <T> ArgumentCommandNode<CommandSource, T>.toPaper(): BrigadierArgumentCommandNode<CommandSourceStack, T> =
    mapSource<CommandSource, CommandSourceStack, T> { it.toAnvilCommandSource() }.toBrigadier()

/**
 * Converts a kbrig literal command node to a paper brigadier literal command node.
 */
fun LiteralCommandNode<CommandSource>.toPaper(): BrigadierLiteralCommandNode1<CommandSourceStack> =
    mapSource<CommandSource, CommandSourceStack> { it.toAnvilCommandSource() }.toBrigadier()

/**
 * Paper requires some extra steps for command registration.
 *
 * Unfortunately, it is not possible to directly register a brigadier type.
 * It is necessary to register Paper's custom [CommandBuilder].
 *
 * This method effectively clones the target command node into a newly created [CommandBuilder].
 */
fun LiteralCommandNode<CommandSource>.toPaperRoot(plugin: Plugin): CommandBuilder {
    val delegate = toPaper()
    val builder = CommandBuilder.newCommandBuilder(plugin, delegate.literal)
    delegate.children.forEach { builder.then(it) }
    builder.requires(delegate.requirement)
    builder.executes(delegate.command)
    return builder
}
