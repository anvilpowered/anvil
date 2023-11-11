package org.anvilpowered.anvil.sponge.user

import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.spongepowered.api.entity.living.player.server.ServerPlayer

fun ServerPlayer.toAnvilPlayer(): Player = AnvilSpongePlayer(this)

private class AnvilSpongePlayer(
    val spongePlayer: ServerPlayer,
) : Player,
    Subject by spongePlayer.toAnvilSubject() {
}
