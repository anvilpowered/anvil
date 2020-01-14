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

package rocks.milspecsg.mscore.common.coremember;

import com.google.inject.Inject;
import rocks.milspecsg.mscore.api.coremember.CoreMemberManager;
import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.common.manager.CommonManager;

public class CommonCoreMemberManager extends CommonManager<CoreMemberRepository<?, ?>> implements CoreMemberManager {

    @Inject
    public CommonCoreMemberManager(Registry registry) {
        super(registry);
    }
}
