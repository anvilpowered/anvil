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

package org.anvilpowered.anvil.core.command.regedit

import com.google.inject.Inject
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.command.CommandContext
import org.anvilpowered.anvil.api.misc.green
import org.anvilpowered.anvil.api.misc.red
import org.anvilpowered.anvil.api.misc.sendTo

class CommonRegistryEditCancelCommand<TUser, TPlayer, TCommandSource>
    : CommonRegistryEditBaseCommand<TUser, TPlayer, TCommandSource>() {

    @Inject
    private lateinit var registryEditRootCommand: CommonRegistryEditRootCommand<TUser, TPlayer, TCommandSource>

    override fun execute(context: CommandContext<TCommandSource>) {
        val builder = Component.text().append(pluginInfo.prefix)
        val removed = registryEditRootCommand.stages.remove(context.userUUID)
        if (removed == null) {
            builder.append(Component.text().append(Component.text("Could not find stage")).red())
        } else {
            builder.append(Component.text().append(Component.text("Successfully cancelled changes. Didn't mean to? ")).green())
                .append(undo("/$anvilAlias regedit start ${removed.envName}"))
        }.sendTo(context.source)
    }
}
