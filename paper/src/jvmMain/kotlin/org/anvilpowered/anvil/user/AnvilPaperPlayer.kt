package org.anvilpowered.anvil.user

import org.anvilpowered.anvil.core.db.RepositoryScopeImpl
import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User
import org.bukkit.entity.Player as PaperPlayer

fun PaperPlayer.toAnvilPlayer(): Player = AnvilPaperPlayer(this)

private class AnvilPaperPlayer(
    val paperPlayer: PaperPlayer,
) : Player,
    Audience by paperPlayer,
    Subject by paperPlayer.toAnvilSubject() {
    override val gameUser: GameUser = GameUser(paperPlayer.uniqueId)

    override suspend fun getUserOrNull(): User? =
        RepositoryScopeImpl.userRepository.findByGameUserId(paperPlayer.uniqueId)
}
