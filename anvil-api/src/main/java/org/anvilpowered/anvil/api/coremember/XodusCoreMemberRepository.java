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

package org.anvilpowered.anvil.api.coremember;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.anvilpowered.anvil.api.datastore.XodusRepository;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface XodusCoreMemberRepository
    extends CoreMemberRepository<EntityId, PersistentEntityStore>,
    XodusRepository<CoreMember<EntityId>> {

    /**
     * Creates a {@link Function} that acts as a {@code query}
     * matching documents whose property {@code userUUID}
     * matches the provided {@link UUID}
     *
     * @param userUUID {@link UUID} to create {@code query} for
     * @return {@code query} for the provided {@link UUID}
     */
    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(UUID userUUID);

    /**
     * Creates a {@link Function} that acts as a {@code query}
     * matching documents whose property {@code userName}
     * matches the provided {@link String}
     *
     * @param userName {@link String} to create {@code query} for
     * @return {@code query} for the provided {@link UUID}
     */
    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(String userName);

    /**
     * Creates a {@link Function} that acts as a {@code query}
     * matching documents whose property {@code ipAddress}
     * matches the provided {@link String}
     *
     * @param ipAddress {@link String} to create {@code query} for
     * @return {@code query} for the provided {@link UUID}
     */
    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForIpAddress(String ipAddress);

    /**
     * Updates the properties {@code banEndUtc}, {@code banReason}
     * and sets {@code banned} to {@code true} for documents that
     * match the provided {@code query}
     *
     * @param query  {@code query} to update documents for
     * @param endUtc {@link Instant} end of the ban
     * @param reason {@link String} reason for the ban
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> ban(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant endUtc, String reason);

    /**
     * Sets the property {@code banned} to {@code false} for
     * documents that match the provided {@code query}
     *
     * @param query {@code query} to update documents for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unBan(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    /**
     * Updates the properties {@code muteEndUtc}, {@code muteReason}
     * and sets {@code muted} to {@code true} for documents that
     * match the provided {@code query}
     *
     * @param query  {@code query} to update documents for
     * @param endUtc {@link Instant} end of the mute
     * @param reason {@link String} reason for the mute
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> mute(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant endUtc, String reason);

    /**
     * Sets the property {@code muted} to {@code false} for
     * documents that match the provided {@code query}
     *
     * @param query {@code query} to update documents for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unMute(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    /**
     * Updates the property {@code nickName} for
     * documents that match the provided {@code query}
     *
     * @param query    {@code query} to update documents for
     * @param nickName {@link String} new nickName
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> setNickName(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, String nickName);

    CompletableFuture<Boolean> deleteNickName(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);
}
