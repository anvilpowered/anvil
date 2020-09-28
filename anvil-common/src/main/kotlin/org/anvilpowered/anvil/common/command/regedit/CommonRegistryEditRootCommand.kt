/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

import java.util.UUID

open class CommonRegistryEditRootCommand<TUser, TPlayer, TString, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TString, TCommandSource>() {

    val stages: MutableMap<UUID, Stage<TString, TCommandSource>> = HashMap()

    val notInStage: TString
        get() = textService.builder()
            .append(pluginInfo.prefix)
            .red().append("You are not currently in a regedit session. Use /$alias regedit help")
            .build()

    fun execute(source: TCommandSource, context: Array<String>? = null) {
        if (hasNoPerms(source)) return
        textService.send(stages[userService.getUUIDSafe(source)]?.print() ?: notInStage, source)
    }
}
