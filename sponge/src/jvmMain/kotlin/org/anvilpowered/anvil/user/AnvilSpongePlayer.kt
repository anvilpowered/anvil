package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.core.db.RepositoryScopeImpl
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer

fun ServerPlayer.toAnvilPlayer(): Player = AnvilSpongePlayer(this)

private class AnvilSpongePlayer(
    val spongePlayer: ServerPlayer,
) : Player,
    Subject by spongePlayer.toAnvilSubject() {
    override val gameUser: GameUser = GameUser(spongePlayer.uniqueId())

    override suspend fun getUserOrNull(): User? =
        RepositoryScopeImpl.userRepository.findByGameUserId(spongePlayer.uniqueId())
}
