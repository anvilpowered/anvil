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

import com.google.inject.Inject

open class CommonRegistryEditCommitCommand<TUser, TPlayer, TString, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TString, TCommandSource>() {

    @Inject
    private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TString, TCommandSource>

    fun execute(source: TCommandSource, context: Array<String>? = null) {
        if (hasNoPerms(source)) return
        val uuid = userService.getUUIDSafe(source)
        val stage = registryEditRootCommand.stages[uuid]
        if (stage == null) {
            textService.send(registryEditRootCommand.notInStage, source)
            return
        }
        if (stage.commit(source)) {
            registryEditRootCommand.stages.remove(uuid)
        }
    }
}
