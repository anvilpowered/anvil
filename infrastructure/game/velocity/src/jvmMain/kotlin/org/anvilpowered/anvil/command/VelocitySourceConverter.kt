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

@file:JvmName("VelocitySourceConverter")

package org.anvilpowered.anvil.command

import org.anvilpowered.anvil.domain.user.CommandSource
import org.anvilpowered.anvil.user.toAnvil
import org.anvilpowered.kbrig.tree.ArgumentCommandNode
import org.anvilpowered.kbrig.tree.mapSource
import com.velocitypowered.api.command.CommandSource as VelocityCommandSource

fun <T> ArgumentCommandNode<CommandSource, T>.mapToVelocitySource(): ArgumentCommandNode<VelocityCommandSource, T> =
    mapSource { it.toAnvil() }
