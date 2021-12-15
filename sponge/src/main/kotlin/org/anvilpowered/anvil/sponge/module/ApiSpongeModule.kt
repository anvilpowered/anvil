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
package org.anvilpowered.anvil.sponge.module

import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.entity.RestrictionService
import org.anvilpowered.anvil.api.misc.bind
import org.anvilpowered.anvil.api.misc.to
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.api.util.AudienceService
import org.anvilpowered.anvil.api.util.KickService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.PlatformImpl
import org.anvilpowered.anvil.common.entity.CommonRestrictionService
import org.anvilpowered.anvil.common.util.SendTextService
import org.anvilpowered.anvil.sponge.command.SpongeCommandExecuteService
import org.anvilpowered.anvil.sponge.command.SpongeSimpleCommandService
import org.anvilpowered.anvil.sponge.server.Sponge8LocationService
import org.anvilpowered.anvil.sponge.util.Log4jAdapter
import org.anvilpowered.anvil.sponge.util.SpongeAudienceService
import org.anvilpowered.anvil.sponge.util.SpongeKickService
import org.anvilpowered.anvil.sponge.util.SpongeSendTextService
import org.anvilpowered.anvil.sponge.util.SpongeUserService
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer

class ApiSpongeModule : ApiSpongeModule(
    PlatformImpl(
        "sponge",
        false,
        //TODO Fix the version
        {
            "${Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().version().majorVersion}.${
                Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().version().minorVersion
            } "
        },
        Log4jAdapter::bindLogger,
    )
) {

    override fun configure() {
        super.configure()
        with(binder()) {
            bind<RestrictionService>().to<CommonRestrictionService>()
            bind<UserService<User, ServerPlayer>>().to<SpongeUserService>()
            bind<SendTextService<CommandCause>>().to<SpongeSendTextService>()
            bind<SendTextService<*>>().to<SpongeSendTextService>()
            bind<TextService<CommandCause>>().to<CommonTextService<CommandCause>>()
            bind<TextService<*>>().to<CommonTextService<*>>()
            bind<AudienceService<CommandCause>>().to<SpongeAudienceService>()
            bind<CommandExecuteService>().to<SpongeCommandExecuteService>()
            bind<KickService>().to<SpongeKickService>()
            bind<LocationService>().to<Sponge8LocationService>()
            bind<SimpleCommandService<CommandCause>>().to<SpongeSimpleCommandService>()
            bind(EntityUtils::class.java).to(SpongeEntityUtils::class.java)
            bind(PermissionService::class.java).to(SpongePermissionService::class.java)
        }
    }
}
