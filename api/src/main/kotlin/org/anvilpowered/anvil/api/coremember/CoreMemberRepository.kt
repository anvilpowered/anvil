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
package org.anvilpowered.anvil.api.coremember

import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.time.Instant
import org.anvilpowered.anvil.api.model.coremember.CoreMember
import org.anvilpowered.anvil.api.datastore.Repository
import java.util.Optional

interface CoreMemberRepository<TKey, TDataStore> : Repository<TKey, CoreMember<TKey>, TDataStore> {
  /**
   * Ensures that a matching [CoreMember] exists in the database.
   *
   *
   *
   * If the userName has changed since the last time this method was called, it will be updated in the database
   *
   *
   *
   * The boolean values in `flags` will be set according to the following rules:
   *
   *
   *
   * 0 : Whether a new member was created in the database
   *
   *
   *
   * 1 : Whether userName was updated in the database
   *
   *
   *
   * 2 : Whether ipAddress was updated in the database
   *
   *
   *
   * `flags` must be an array of length 8.
   * There are currently 5 elements reserved for future use
   *
   *
   * @param userUUID  [UUID] userUUID of user.
   * @param userName  [String] Name of user.
   * @param ipAddress [String] IP Address of user.
   * @param flags     A boolean array of length 8.
   * @return An [Optional] containing the inserted [CoreMember] if successful, otherwise [Optional.empty]
   * @throws IllegalArgumentException If `flags` is not of length 8
   */
  fun getOneOrGenerateForUser(
    userUUID: UUID,
    userName: String,
    ipAddress: String,
    flags: BooleanArray
  ): CompletableFuture<Optional<CoreMember<TKey>>>

  /**
   * Ensures that a matching [CoreMember] exists in the database.
   *
   *
   *
   * If the userName has changed since the last time this method was called, it will be updated in the database
   *
   *
   * @param userUUID  [UUID] userUUID of user
   * @param userName  [String] Name of user
   * @param ipAddress [String] IP Address of user
   * @return An [Optional] containing the inserted [CoreMember] if successful, otherwise [Optional.empty]
   */
  fun getOneOrGenerateForUser(
    userUUID: UUID,
    userName: String,
    ipAddress: String
  ): CompletableFuture<Optional<CoreMember<TKey>>>

  /**
   * @param userUUID [UUID] userUUID of user
   * @return An [Optional] containing a matching [CoreMember] if successful, otherwise [Optional.empty]
   */
  fun getOneForUser(userUUID: UUID): CompletableFuture<Optional<CoreMember<TKey>>>

  /**
   * @param userName [String] Name of user
   * @return An [Optional] containing a matching [CoreMember] if successful, otherwise [Optional.empty]
   */
  fun getOneForUser(userName: String): CompletableFuture<Optional<CoreMember<TKey>>>

  /**
   * @param ipAddress [String] IP Address of user
   * @return A [List] of matching [CoreMember] if successful, otherwise [Optional.empty]
   */
  fun getForIpAddress(ipAddress: String): CompletableFuture<List<CoreMember<TKey>>>

  /**
   * Updates the properties `banEndUtc`, `banReason`
   * and sets `banned` to `true` for the document
   * whose id matches the provided [TKey]
   *
   * @param id     [TKey] id of document to update
   * @param endUtc [Instant] end of the ban
   * @param reason [String] reason for the ban
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun ban(id: TKey, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `banEndUtc`, `banReason`
   * and sets `banned` to `true` for documents
   * whose property `userUUID` matches the provided [UUID]
   *
   * @param userUUID [UUID] userUUID of documents to update
   * @param endUtc   [Instant] end of the ban
   * @param reason   [String] reason for the ban
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun banUser(userUUID: UUID, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `banEndUtc`, `banReason`
   * and sets `banned` to `true` for documents
   * whose property `userName` matches the provided [String]
   *
   * @param userName [String] userName of documents to update
   * @param endUtc   [Instant] end of the ban
   * @param reason   [String] reason for the ban
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun banUser(userName: String, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `banEndUtc`, `banReason`
   * and sets `banned` to `true` for documents
   * whose property `ipAddress` matches the provided [String]
   *
   * @param ipAddress [String] ipAddress of documents to update
   * @param endUtc    [Instant] end of the ban
   * @param reason    [String] reason for the ban
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun banIpAddress(ipAddress: String, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Sets the property `banned` to `false` for
   * the document whose id matches the provided [TKey]
   *
   * @param id [TKey] id of document to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unBan(id: TKey): CompletableFuture<Boolean>

  /**
   * Sets the property `banned` to `false` for
   * documents whose property `userUUID` matches
   * the provided [UUID]
   *
   * @param userUUID [UUID] userUUID of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unBanUser(userUUID: UUID): CompletableFuture<Boolean>

  /**
   * Sets the property `banned` to `false` for
   * documents whose property `userName` matches
   * the provided [String]
   *
   * @param userName [String] userName of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unBanUser(userName: String): CompletableFuture<Boolean>

  /**
   * Sets the property `banned` to `false` for
   * documents whose property `ipAddress` matches
   * the provided [String]
   *
   * @param ipAddress [String] ipAddress of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unBanIpAddress(ipAddress: String): CompletableFuture<Boolean>

  /**
   * Verifies whether the provided [CoreMember] is in fact
   * banned.
   *
   *
   *
   * A user is only banned if [CoreMember.isBanned] is
   * true and [CoreMember.getBanEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isBanned] and
   * [CoreMember.getBanEndUtc] and sets the member's
   * ban status accordingly.
   *
   *
   * @param coreMember [CoreMember] to verify ban for
   * @return `true` if user is verified to be banned, otherwise `false`
   */
  fun checkBanned(coreMember: CoreMember<*>): Boolean

  /**
   * Verifies whether the [CoreMember] matching the provided
   * [TKey] is in fact banned.
   *
   *
   *
   * A user is only banned if [CoreMember.isBanned] is
   * true and [CoreMember.getBanEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isBanned] and
   * [CoreMember.getBanEndUtc] and sets the member's
   * ban status accordingly.
   *
   *
   * @param id [TKey] id of member to verify ban for
   * @return [CompletableFuture] wrapped [Boolean].
   * `true` if user is verified to be banned, otherwise `false`
   */
  fun checkBanned(id: TKey): CompletableFuture<Boolean>

  /**
   * Verifies whether the [CoreMember] matching the provided
   * [UUID] userUUID is in fact banned.
   *
   *
   *
   * A user is only banned if [CoreMember.isBanned] is
   * true and [CoreMember.getBanEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isBanned] and
   * [CoreMember.getBanEndUtc] and sets the member's
   * ban status accordingly.
   *
   *
   * @param userUUID [UUID] userUUID of member to verify ban for
   * @return [CompletableFuture] wrapped [Boolean].
   * `true` if user is verified to be banned, otherwise `false`
   */
  fun checkBannedForUser(userUUID: UUID): CompletableFuture<Boolean>

  /**
   * Verifies whether the [CoreMember] matching the provided
   * [String] userName is in fact banned.
   *
   *
   *
   * A user is only banned if [CoreMember.isBanned] is
   * true and [CoreMember.getBanEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isBanned] and
   * [CoreMember.getBanEndUtc] and sets the member's
   * ban status accordingly.
   *
   *
   * @param userName [String] userUUID of member to verify ban for
   * @return [CompletableFuture] wrapped [Boolean].
   * `true` if user is verified to be banned, otherwise `false`
   */
  fun checkBannedForUser(userName: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `muteEndUtc`, `muteReason`
   * and sets `muted` to `true` for the document
   * whose id matches the provided [TKey]
   *
   * @param id     [TKey] id of document to update
   * @param endUtc [Instant] end of the mute
   * @param reason [String] reason for the mute
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun mute(id: TKey, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `muteEndUtc`, `muteReason`
   * and sets `muted` to `true` for documents
   * whose property `userUUID` matches the provided [UUID]
   *
   * @param userUUID [UUID] userUUID of documents to update
   * @param endUtc   [Instant] end of the mute
   * @param reason   [String] reason for the mute
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun muteUser(userUUID: UUID, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `muteEndUtc`, `muteReason`
   * and sets `muted` to `true` for documents
   * whose property `userName` matches the provided [String]
   *
   * @param userName [String] userName of documents to update
   * @param endUtc   [Instant] end of the mute
   * @param reason   [String] reason for the mute
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun muteUser(userName: String, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Updates the properties `muteEndUtc`, `muteReason`
   * and sets `muted` to `true` for documents
   * whose property `ipAddress` matches the provided [String]
   *
   * @param ipAddress [String] ipAddress of documents to update
   * @param endUtc    [Instant] end of the mute
   * @param reason    [String] reason for the mute
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun muteIpAddress(ipAddress: String, endUtc: Instant, reason: String): CompletableFuture<Boolean>

  /**
   * Sets the property `muted` to `false` for
   * the document whose id matches the provided [TKey]
   *
   * @param id [TKey] id of document to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unMute(id: TKey): CompletableFuture<Boolean>

  /**
   * Sets the property `muted` to `false` for
   * documents whose property `userUUID` matches
   * the provided [UUID]
   *
   * @param userUUID [UUID] userUUID of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unMuteUser(userUUID: UUID): CompletableFuture<Boolean>

  /**
   * Sets the property `muted` to `false` for
   * documents whose property `userName` matches
   * the provided [String]
   *
   * @param userName [String] userName of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unMuteUser(userName: String): CompletableFuture<Boolean>

  /**
   * Sets the property `muted` to `false` for
   * documents whose property `ipAddress` matches
   * the provided [String]
   *
   * @param ipAddress [String] ipAddress of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun unMuteIpAddress(ipAddress: String): CompletableFuture<Boolean>

  /**
   * Verifies whether the provided [CoreMember] is in fact
   * muted.
   *
   *
   *
   * A user is only muted if [CoreMember.isMuted] is
   * true and [CoreMember.getMuteEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isMuted] and
   * [CoreMember.getMuteEndUtc] and sets the member's
   * mute status accordingly.
   *
   *
   * @param coreMember [CoreMember] to verify mute for
   * @return `true` if user is verified to be muted, otherwise `false`
   */
  fun checkMuted(coreMember: CoreMember<*>): Boolean

  /**
   * Verifies whether the [CoreMember] matching the provided
   * [TKey] is in fact muted.
   *
   *
   *
   * A user is only muted if [CoreMember.isMuted] is
   * true and [CoreMember.getMuteEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isMuted] and
   * [CoreMember.getMuteEndUtc] and sets the member's
   * mute status accordingly.
   *
   *
   * @param id [TKey] id of member to verify mute for
   * @return [CompletableFuture] wrapped [Boolean].
   * `true` if user is verified to be muted, otherwise `false`
   */
  fun checkMuted(id: TKey): CompletableFuture<Boolean>

  /**
   * Verifies whether the [CoreMember] matching the provided
   * [UUID] userUUID is in fact muted.
   *
   *
   *
   * A user is only muted if [CoreMember.isMuted] is
   * true and [CoreMember.getMuteEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isMuted] and
   * [CoreMember.getMuteEndUtc] and sets the member's
   * mute status accordingly.
   *
   *
   * @param userUUID [UUID] userUUID of member to verify mute for
   * @return [CompletableFuture] wrapped [Boolean].
   * `true` if user is verified to be muted, otherwise `false`
   */
  fun checkMutedForUser(userUUID: UUID): CompletableFuture<Boolean>

  /**
   * Verifies whether the [CoreMember] matching the provided
   * [String] userName is in fact muted.
   *
   *
   *
   * A user is only muted if [CoreMember.isMuted] is
   * true and [CoreMember.getMuteEndUtc] is in the future.
   *
   *
   *
   *
   * This method checks both [CoreMember.isMuted] and
   * [CoreMember.getMuteEndUtc] and sets the member's
   * mute status accordingly.
   *
   *
   * @param userUUID [UUID] userUUID of member to verify mute for
   * @return [CompletableFuture] wrapped [Boolean].
   * `true` if user is verified to be muted, otherwise `false`
   */
  fun checkMutedForUser(userUUID: String): CompletableFuture<Boolean>

  /**
   * Updates the property `nickName` for the
   * document whose id matches the provided [TKey]
   *
   * @param id       [TKey] id of document to update
   * @param nickName [String] new nickName
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun setNickName(id: TKey, nickName: String): CompletableFuture<Boolean>

  /**
   * Updates the property `nickName` for documents whose
   * property `userUUID` matches the provided [UUID]
   *
   * @param userUUID [UUID] userUUID of documents to update
   * @param nickName [String] new nickName
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun setNickNameForUser(userUUID: UUID, nickName: String): CompletableFuture<Boolean>

  /**
   * Updates the property `nickName` for documents whose
   * property `userName` matches the provided [String]
   *
   * @param userName [String] userName of documents to update
   * @param nickName [String] new nickName
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun setNickNameForUser(userName: String, nickName: String): CompletableFuture<Boolean>

  /**
   * Deletes the property `nickName` for the
   * document whose id matches the provided [TKey]
   *
   * @param id [TKey] id of document to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun deleteNickName(id: TKey): CompletableFuture<Boolean>

  /**
   * Deletes the property `nickName` for documents whose
   * property `userUUID` matches the provided [UUID]
   *
   * @param userUUID [UUID] userName of documents to update
   * @return [CompletableFuture] wrapped [Boolean].
   * true if successful, otherwise false
   */
  fun deleteNickNameForUser(userUUID: UUID): CompletableFuture<Boolean>

  /**
   * Deletes the property `nickName` for documents whose
   * property `userName` matches the provided [String]
   *
   * @param userName [String] userName of documents to update
   * @return [CompletableFuture] wrapped [boolean]
   * true if successful, otherwise false
   */
  fun deleteNickNameForUser(userName: String): CompletableFuture<Boolean>
}
