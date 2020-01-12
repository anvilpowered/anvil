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

package rocks.milspecsg.mscore.service.common.coremember.repository;

import rocks.milspecsg.mscore.api.coremember.repository.CoreMemberRepository;
import rocks.milspecsg.mscore.model.core.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.service.common.repository.CommonRepository;

public abstract class CommonCoreMemberRepository<
    TKey,
    TDataStore>
    extends CommonRepository<TKey, CoreMember<TKey>, CacheService<TKey, CoreMember<TKey>, TDataStore>, TDataStore>
    implements CoreMemberRepository<TKey, TDataStore> {

    protected CommonCoreMemberRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<CoreMember<TKey>> getTClass() {
        return (Class<CoreMember<TKey>>) getDataStoreContext().getEntityClassUnsafe("coremember");
    }
}
