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

package org.anvilpowered.anvil.common.command.regedit

import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.red
import org.anvilpowered.anvil.api.sendTo
import java.util.UUID

class CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

    val stages: MutableMap<UUID, Stage<TCommandSource>> = HashMap()

    val notInStage by lazy {
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text("You are not currently in a regedit session. Use /$anvilAlias regedit help")
                .red())
            .build()
    }

    override fun execute(source: TCommandSource, context: Array<String>) {
        stages[userService.getUUIDSafe(source)]?.print()?.sendTo(source) ?: notInStage.sendTo(source)
    }
}
