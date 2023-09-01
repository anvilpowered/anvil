package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.core.db.RepositoryScopeImpl
import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User
import com.velocitypowered.api.proxy.Player as VelocityPlayer

fun VelocityPlayer.toAnvilPlayer(): Player = AnvilVelocityPlayer(this)

private class AnvilVelocityPlayer(
    val velocityPlayer: VelocityPlayer,
) : Player,
    Audience by velocityPlayer,
    Subject by velocityPlayer.toAnvilSubject() {

    override val gameUser: GameUser = GameUser(velocityPlayer.uniqueId)

    override suspend fun getUserOrNull(): User? =
        RepositoryScopeImpl.userRepository.findByGameUserId(velocityPlayer.uniqueId)
}
