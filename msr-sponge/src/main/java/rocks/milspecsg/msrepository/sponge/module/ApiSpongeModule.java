/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msrepository.sponge.module;

import com.google.inject.TypeLiteral;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.util.KickService;
import rocks.milspecsg.msrepository.api.util.StringResult;
import rocks.milspecsg.msrepository.api.util.TeleportationService;
import rocks.milspecsg.msrepository.api.util.UserService;
import rocks.milspecsg.msrepository.common.module.ApiCommonModule;
import rocks.milspecsg.msrepository.sponge.util.SpongeKickService;
import rocks.milspecsg.msrepository.sponge.util.SpongeStringResult;
import rocks.milspecsg.msrepository.sponge.util.SpongeTeleportationService;
import rocks.milspecsg.msrepository.sponge.util.SpongeUserService;

public class ApiSpongeModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(KickService.class).to(SpongeKickService.class);
        bind(new TypeLiteral<StringResult<Text, CommandSource>>() {
        }).to(SpongeStringResult.class);
        bind(TeleportationService.class).to(SpongeTeleportationService.class);
        bind(new TypeLiteral<UserService<User, Player>>() {
        }).to(SpongeUserService.class);
    }
}
