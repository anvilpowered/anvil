/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

import org.anvilpowered.anvil.api.datastore.Repository;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CoreMemberRepository<
    TKey,
    TDataStore>
    extends Repository<TKey, CoreMember<TKey>, TDataStore> {

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
     * @param userUUID  {@link UUID} userUUID of user.
     * @param userName  {@link String} Name of user.
     * @param ipAddress {@link String} IP Address of user.
     * @param flags     A boolean array of length 8.
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
     * @param userUUID  {@link UUID} userUUID of user
     * @param userName  {@link String} Name of user
     * @param ipAddress {@link String} IP Address of user
     * @return An {@link Optional} containing the inserted {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress);

    /**
     * @param userUUID {@link UUID} userUUID of user
     * @return An {@link Optional} containing a matching {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneForUser(UUID userUUID);

    /**
     * @param userName {@link String} Name of user
     * @return An {@link Optional} containing a matching {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<Optional<CoreMember<TKey>>> getOneForUser(String userName);

    /**
     * @param ipAddress {@link String} IP Address of user
     * @return A {@link List} of matching {@link CoreMember} if successful, otherwise {@link Optional#empty()}
     */
    CompletableFuture<List<CoreMember<TKey>>> getForIpAddress(String ipAddress);

    /**
     * Updates the properties {@code banEndUtc}, {@code banReason}
     * and sets {@code banned} to {@code true} for the document
     * whose id matches the provided {@link TKey}
     *
     * @param id     {@link TKey} id of document to update
     * @param endUtc {@link Instant} end of the ban
     * @param reason {@link String} reason for the ban
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> ban(TKey id, Instant endUtc, String reason);

    /**
     * Updates the properties {@code banEndUtc}, {@code banReason}
     * and sets {@code banned} to {@code true} for documents
     * whose property {@code userUUID} matches the provided {@link UUID}
     *
     * @param userUUID {@link UUID} userUUID of documents to update
     * @param endUtc   {@link Instant} end of the ban
     * @param reason   {@link String} reason for the ban
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> banUser(UUID userUUID, Instant endUtc, String reason);

    /**
     * Updates the properties {@code banEndUtc}, {@code banReason}
     * and sets {@code banned} to {@code true} for documents
     * whose property {@code userName} matches the provided {@link String}
     *
     * @param userName {@link String} userName of documents to update
     * @param endUtc   {@link Instant} end of the ban
     * @param reason   {@link String} reason for the ban
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> banUser(String userName, Instant endUtc, String reason);

    /**
     * Updates the properties {@code banEndUtc}, {@code banReason}
     * and sets {@code banned} to {@code true} for documents
     * whose property {@code ipAddress} matches the provided {@link String}
     *
     * @param ipAddress {@link String} ipAddress of documents to update
     * @param endUtc    {@link Instant} end of the ban
     * @param reason    {@link String} reason for the ban
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> banIpAddress(String ipAddress, Instant endUtc, String reason);

    /**
     * Sets the property {@code banned} to {@code false} for
     * the document whose id matches the provided {@link TKey}
     *
     * @param id {@link TKey} id of document to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unBan(TKey id);

    /**
     * Sets the property {@code banned} to {@code false} for
     * documents whose property {@code userUUID} matches
     * the provided {@link UUID}
     *
     * @param userUUID {@link UUID} userUUID of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unBanUser(UUID userUUID);

    /**
     * Sets the property {@code banned} to {@code false} for
     * documents whose property {@code userName} matches
     * the provided {@link String}
     *
     * @param userName {@link String} userName of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unBanUser(String userName);

    /**
     * Sets the property {@code banned} to {@code false} for
     * documents whose property {@code ipAddress} matches
     * the provided {@link String}
     *
     * @param ipAddress {@link String} ipAddress of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unBanIpAddress(String ipAddress);

    /**
     * Verifies whether the provided {@link CoreMember} is in fact
     * banned.
     *
     * <p>
     * A user is only banned if {@link CoreMember#isBanned()} is
     * true and {@link CoreMember#getBanEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isBanned()} and
     * {@link CoreMember#getBanEndUtc()} and sets the member's
     * ban status accordingly.
     * </p>
     *
     * @param coreMember {@link CoreMember} to verify ban for
     * @return {@code true} if user is verified to be banned, otherwise {@code false}
     */
    boolean checkBanned(CoreMember<?> coreMember);

    /**
     * Verifies whether the {@link CoreMember} matching the provided
     * {@link TKey} is in fact banned.
     *
     * <p>
     * A user is only banned if {@link CoreMember#isBanned()} is
     * true and {@link CoreMember#getBanEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isBanned()} and
     * {@link CoreMember#getBanEndUtc()} and sets the member's
     * ban status accordingly.
     * </p>
     *
     * @param id {@link TKey} id of member to verify ban for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * {@code true} if user is verified to be banned, otherwise {@code false}
     */
    CompletableFuture<Boolean> checkBanned(TKey id);

    /**
     * Verifies whether the {@link CoreMember} matching the provided
     * {@link UUID} userUUID is in fact banned.
     *
     * <p>
     * A user is only banned if {@link CoreMember#isBanned()} is
     * true and {@link CoreMember#getBanEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isBanned()} and
     * {@link CoreMember#getBanEndUtc()} and sets the member's
     * ban status accordingly.
     * </p>
     *
     * @param userUUID {@link UUID} userUUID of member to verify ban for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * {@code true} if user is verified to be banned, otherwise {@code false}
     */
    CompletableFuture<Boolean> checkBannedForUser(UUID userUUID);

    /**
     * Verifies whether the {@link CoreMember} matching the provided
     * {@link String} userName is in fact banned.
     *
     * <p>
     * A user is only banned if {@link CoreMember#isBanned()} is
     * true and {@link CoreMember#getBanEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isBanned()} and
     * {@link CoreMember#getBanEndUtc()} and sets the member's
     * ban status accordingly.
     * </p>
     *
     * @param userName {@link String} userUUID of member to verify ban for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * {@code true} if user is verified to be banned, otherwise {@code false}
     */
    CompletableFuture<Boolean> checkBannedForUser(String userName);

    /**
     * Updates the properties {@code muteEndUtc}, {@code muteReason}
     * and sets {@code muted} to {@code true} for the document
     * whose id matches the provided {@link TKey}
     *
     * @param id     {@link TKey} id of document to update
     * @param endUtc {@link Instant} end of the mute
     * @param reason {@link String} reason for the mute
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> mute(TKey id, Instant endUtc, String reason);

    /**
     * Updates the properties {@code muteEndUtc}, {@code muteReason}
     * and sets {@code muted} to {@code true} for documents
     * whose property {@code userUUID} matches the provided {@link UUID}
     *
     * @param userUUID {@link UUID} userUUID of documents to update
     * @param endUtc   {@link Instant} end of the mute
     * @param reason   {@link String} reason for the mute
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> muteUser(UUID userUUID, Instant endUtc, String reason);

    /**
     * Updates the properties {@code muteEndUtc}, {@code muteReason}
     * and sets {@code muted} to {@code true} for documents
     * whose property {@code userName} matches the provided {@link String}
     *
     * @param userName {@link String} userName of documents to update
     * @param endUtc   {@link Instant} end of the mute
     * @param reason   {@link String} reason for the mute
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> muteUser(String userName, Instant endUtc, String reason);

    /**
     * Updates the properties {@code muteEndUtc}, {@code muteReason}
     * and sets {@code muted} to {@code true} for documents
     * whose property {@code ipAddress} matches the provided {@link String}
     *
     * @param ipAddress {@link String} ipAddress of documents to update
     * @param endUtc    {@link Instant} end of the mute
     * @param reason    {@link String} reason for the mute
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> muteIpAddress(String ipAddress, Instant endUtc, String reason);

    /**
     * Sets the property {@code muted} to {@code false} for
     * the document whose id matches the provided {@link TKey}
     *
     * @param id {@link TKey} id of document to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unMute(TKey id);

    /**
     * Sets the property {@code muted} to {@code false} for
     * documents whose property {@code userUUID} matches
     * the provided {@link UUID}
     *
     * @param userUUID {@link UUID} userUUID of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unMuteUser(UUID userUUID);

    /**
     * Sets the property {@code muted} to {@code false} for
     * documents whose property {@code userName} matches
     * the provided {@link String}
     *
     * @param userName {@link String} userName of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unMuteUser(String userName);

    /**
     * Sets the property {@code muted} to {@code false} for
     * documents whose property {@code ipAddress} matches
     * the provided {@link String}
     *
     * @param ipAddress {@link String} ipAddress of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> unMuteIpAddress(String ipAddress);

    /**
     * Verifies whether the provided {@link CoreMember} is in fact
     * muted.
     *
     * <p>
     * A user is only muted if {@link CoreMember#isMuted()} is
     * true and {@link CoreMember#getMuteEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isMuted()} and
     * {@link CoreMember#getMuteEndUtc()} and sets the member's
     * mute status accordingly.
     * </p>
     *
     * @param coreMember {@link CoreMember} to verify mute for
     * @return {@code true} if user is verified to be muted, otherwise {@code false}
     */
    boolean checkMuted(CoreMember<?> coreMember);

    /**
     * Verifies whether the {@link CoreMember} matching the provided
     * {@link TKey} is in fact muted.
     *
     * <p>
     * A user is only muted if {@link CoreMember#isMuted()} is
     * true and {@link CoreMember#getMuteEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isMuted()} and
     * {@link CoreMember#getMuteEndUtc()} and sets the member's
     * mute status accordingly.
     * </p>
     *
     * @param id {@link TKey} id of member to verify mute for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * {@code true} if user is verified to be muted, otherwise {@code false}
     */
    CompletableFuture<Boolean> checkMuted(TKey id);

    /**
     * Verifies whether the {@link CoreMember} matching the provided
     * {@link UUID} userUUID is in fact muted.
     *
     * <p>
     * A user is only muted if {@link CoreMember#isMuted()} is
     * true and {@link CoreMember#getMuteEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isMuted()} and
     * {@link CoreMember#getMuteEndUtc()} and sets the member's
     * mute status accordingly.
     * </p>
     *
     * @param userUUID {@link UUID} userUUID of member to verify mute for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * {@code true} if user is verified to be muted, otherwise {@code false}
     */
    CompletableFuture<Boolean> checkMutedForUser(UUID userUUID);

    /**
     * Verifies whether the {@link CoreMember} matching the provided
     * {@link String} userName is in fact muted.
     *
     * <p>
     * A user is only muted if {@link CoreMember#isMuted()} is
     * true and {@link CoreMember#getMuteEndUtc()} is in the future.
     * </p>
     *
     * <p>
     * This method checks both {@link CoreMember#isMuted()} and
     * {@link CoreMember#getMuteEndUtc()} and sets the member's
     * mute status accordingly.
     * </p>
     *
     * @param userUUID {@link UUID} userUUID of member to verify mute for
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * {@code true} if user is verified to be muted, otherwise {@code false}
     */
    CompletableFuture<Boolean> checkMutedForUser(String userUUID);

    /**
     * Updates the property {@code nickName} for the
     * document whose id matches the provided {@link TKey}
     *
     * @param id       {@link TKey} id of document to update
     * @param nickName {@link String} new nickName
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> setNickName(TKey id, String nickName);

    /**
     * Updates the property {@code nickName} for documents whose
     * property {@code userUUID} matches the provided {@link UUID}
     *
     * @param userUUID {@link UUID} userUUID of documents to update
     * @param nickName {@link String} new nickName
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> setNickNameForUser(UUID userUUID, String nickName);

    /**
     * Updates the property {@code nickName} for documents whose
     * property {@code userName} matches the provided {@link String}
     *
     * @param userName {@link String} userName of documents to update
     * @param nickName {@link String} new nickName
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> setNickNameForUser(String userName, String nickName);

    /**
     * Deletes the property {@code nickName} for the
     * document whose id matches the provided {@link TKey}
     *
     * @param id {@link TKey} id of document to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> deleteNickName(TKey id);

    /**
     * Deletes the property {@code nickName} for documents whose
     * property {@code userUUID} matches the provided {@link UUID}
     *
     * @param userUUID {@link UUID} userName of documents to update
     * @return {@link CompletableFuture} wrapped {@link Boolean}.
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> deleteNickNameForUser(UUID userUUID);

    /**
     * Deletes the property {@code nickName} for documents whose
     * property {@code userName} matches the provided {@link String}
     *
     * @param userName {@link String} userName of documents to update
     * @return {@link CompletableFuture} wrapped {@link boolean}
     * true if successful, otherwise false
     */
    CompletableFuture<Boolean> deleteNickNameForUser(String userName);
}
