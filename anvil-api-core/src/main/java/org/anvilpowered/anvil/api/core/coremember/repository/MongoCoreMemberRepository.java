/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.api.core.coremember.repository;

import org.anvilpowered.anvil.api.core.model.coremember.CoreMember;
import org.anvilpowered.anvil.api.repository.MongoRepository;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MongoCoreMemberRepository
    extends CoreMemberRepository<ObjectId, Datastore>,
    MongoRepository<CoreMember<ObjectId>> {

    /**
     * Creates a {@link Query} matching documents whose
     * property {@code userUUID} matches the provided {@link UUID}
     *
     * @param userUUID {@link UUID} to create {@link Query} for
     * @return {@link Query} for the provided {@link UUID}
     */
    Query<CoreMember<ObjectId>> asQuery(UUID userUUID);

    /**
     * Creates a {@link Query} matching documents whose
     * property {@code userName} matches the provided {@link String}
     *
     * @param userName {@link String} to create {@link Query} for
     * @return {@link Query} for the provided {@link String}
     */
    Query<CoreMember<ObjectId>> asQuery(String userName);

    /**
     * Creates a {@link Query} matching documents whose
     * property {@code ipAddress} matches the provided {@link String}
     *
     * @param ipAddress {@link String} to create {@link Query} for
     * @return {@link Query} for the provided {@link String}
     */
    Query<CoreMember<ObjectId>> asQueryForIpAddress(String ipAddress);

    CompletableFuture<Optional<BigDecimal>> getBalance(Query<CoreMember<ObjectId>> query);

    CompletableFuture<Boolean> setBalance(Query<CoreMember<ObjectId>> query, BigDecimal balance);

    /**
     * Updates the properties {@code banEndUtc}, {@code banReason}
     * and sets {@code banned} to {@code true} for documents that
     * match the provided {@link Query}
     *
     * @param query  {@link Query} to update documents for
     * @param endUtc {@link Instant} end of the ban
     * @param reason {@link String} reason for the ban
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> ban(Query<CoreMember<ObjectId>> query, Instant endUtc, String reason);

    /**
     * Sets the property {@code banned} to {@code false} for
     * documents that match the provided {@link Query}
     *
     * @param query {@link Query} to update documents for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unBan(Query<CoreMember<ObjectId>> query);

    /**
     * Updates the properties {@code muteEndUtc}, {@code muteReason}
     * and sets {@code muted} to {@code true} for documents that
     * match the provided {@link Query}
     *
     * @param query  {@link Query} to update documents for
     * @param endUtc {@link Instant} end of the mute
     * @param reason {@link String} reason for the mute
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> mute(Query<CoreMember<ObjectId>> query, Instant endUtc, String reason);

    /**
     * Sets the property {@code muted} to {@code false} for
     * documents that match the provided {@link Query}
     *
     * @param query {@link Query} to update documents for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unMute(Query<CoreMember<ObjectId>> query);

    /**
     * Updates the property {@code nickName} for
     * documents that match the provided {@link Query}
     *
     * @param query    {@link Query} to update documents for
     * @param nickName {@link String} new nickName
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> setNickName(Query<CoreMember<ObjectId>> query, String nickName);

    /**
     * Deletes the property {@Code nickName} for
     * documents that match the provided {@link boolean}
     * @param query {@link Query} to update documents for
     * @return {@link CompletableFuture} wrapped {@link Boolean}
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> deleteNickName(Query<CoreMember<ObjectId>> query);
}
