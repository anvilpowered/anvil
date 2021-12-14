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

package org.anvilpowered.anvil.common.coremember;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.coremember.CoreMemberManager;
import org.anvilpowered.anvil.api.coremember.CoreMemberRepository;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.base.datastore.BaseManager;

public class CommonCoreMemberManager extends BaseManager<CoreMemberRepository<?, ?>> implements CoreMemberManager {

    @Inject
    public CommonCoreMemberManager(Registry registry) {
    super(registry);
  }
}
