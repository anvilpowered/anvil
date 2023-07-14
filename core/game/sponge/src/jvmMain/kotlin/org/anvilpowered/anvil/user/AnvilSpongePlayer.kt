package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.db.RepositoryScopeImpl
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer

fun ServerPlayer.toAnvilPlayer(): Player = AnvilSpongePlayer(this)

class AnvilSpongePlayer(
    val spongePlayer: ServerPlayer,
) : Player,
    Subject by spongePlayer.toAnvilSubject() {
    override val gameUser: GameUser
        get() = GameUser(spongePlayer.uniqueId())

    override suspend fun getUserOrNull(): User? =
        RepositoryScopeImpl.userRepository.findByGameId(spongePlayer.uniqueId())
}
