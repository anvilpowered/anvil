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

package rocks.milspecsg.msrepository;

import com.google.inject.TypeLiteral;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.TeleportationService;
import rocks.milspecsg.msrepository.api.UserService;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;
import rocks.milspecsg.msrepository.service.sponge.SpongeTeleportationService;
import rocks.milspecsg.msrepository.service.sponge.SpongeUserService;
import rocks.milspecsg.msrepository.service.sponge.tools.resultbuilder.SpongeStringResult;

public class ApiSpongeModule extends ApiCommonModule {

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<StringResult<Text, CommandSource>>() {
        }).to(SpongeStringResult.class);
        bind(TeleportationService.class).to(SpongeTeleportationService.class);
        bind(new TypeLiteral<UserService<User>>() {
        }).to(SpongeUserService.class);
    }
}
