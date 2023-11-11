package org.anvilpowered.anvil.sponge.user

import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import java.util.UUID

fun ServerPlayer.toAnvilPlayer(): Player = AnvilSpongePlayer(this)

private class AnvilSpongePlayer(
    val spongePlayer: ServerPlayer,
) : Player,
    Subject by spongePlayer.toAnvilSubject() {
    override val id: UUID = spongePlayer.uniqueId()
}
