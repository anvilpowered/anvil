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
package org.anvilpowered.anvil.paper.module

import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.entity.RestrictionService
import org.anvilpowered.anvil.api.misc.bind
import org.anvilpowered.anvil.api.misc.to
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.*
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.entity.CommonRestrictionService
import org.anvilpowered.anvil.common.entity.EntityUtils
import org.anvilpowered.anvil.common.module.JavaUtilLoggingAdapter
import org.anvilpowered.anvil.common.module.PlatformModule
import org.anvilpowered.anvil.api.util.SendTextService
import org.anvilpowered.anvil.paper.command.PaperCommandExecuteService
import org.anvilpowered.anvil.paper.command.PaperSimpleCommandService
import org.anvilpowered.anvil.paper.entity.PaperEntityUtils
import org.anvilpowered.anvil.paper.server.PaperLocationService
import org.anvilpowered.anvil.paper.util.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ApiPaperModule : PlatformModule(
    PlatformImpl(
        "paper",
        false,
        { Bukkit.getVersion() },
        JavaUtilLoggingAdapter::bindLogger,
    )
) {
    override fun configure() {
        super.configure()
        with(binder()) {
            bind<AudienceService<CommandSender>>().to<PaperAudienceService>()
            bind<CommandExecuteService>().to<PaperCommandExecuteService>()
            bind<EntityUtils>().to<PaperEntityUtils>()
            bind<KickService>().to<PaperKickService>()
            bind<LocationService>().to<PaperLocationService>()
            bind<PermissionService>().to<PaperPermissionService>()
            bind<SimpleCommandService<*>>().to<PaperSimpleCommandService>()
            bind<SimpleCommandService<CommandSender>>().to<PaperSimpleCommandService>()
            bind<SendTextService>().to<PaperSendTextService>()
            bind<UserService<Player, Player>>().to<PaperUserService>()
        }
    }
}
