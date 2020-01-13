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

package rocks.milspecsg.mscore.api.coremember.repository;

import rocks.milspecsg.mscore.model.core.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CoreMemberRepository<
    TKey,
    TDataStore>
    extends Repository<TKey, CoreMember<TKey>, CacheService<TKey, CoreMember<TKey>, TDataStore>, TDataStore> {

    /**
     * Ensures that a matching {@link CoreMember} exists in the database.
     *
     * <p>
     * If the userName has changed since the last time this method was called, it will be updated in the database
     * </p>
     * <p>
     * The boolean values in {@code flags} will be set according to the following rules:
     * </p>
     * <p>
     * 0 : Whether a new member was created in the database
     * </p>
     * <p>
     * 1 : Whether userName was updated in the database
     * </p>
     * <p>
     * 2 : Whether ipAddress was updated in the database
     * </p>
     * <p>
     * {@code flags} must be an array of length 8.
     * There are currently 5 elements reserved for future use
     * </p>
     *
     * @return An {@link Optional} containing the inserted {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     * @throws IllegalArgumentException If {@code flags} is not of length 8
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress, boolean[] flags);

    /**
     * Ensures that a matching {@link CoreMember} exists in the database.
     *
     * <p>
     * If the userName has changed since the last time this method was called, it will be updated in the database
     * </p>
     *
     * @return An {@link Optional} containing the inserted {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress);

    /**
     * @return An {@link Optional} containing a matching {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneForUser(UUID userUUID);

    /**
     * @return An {@link Optional} containing a matching {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneForUser(String userName);

    /**
     * @return A {@link List} of matching {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<List<CoreMember<TKey>>> getForIpAddress(String ipAddress);
}
