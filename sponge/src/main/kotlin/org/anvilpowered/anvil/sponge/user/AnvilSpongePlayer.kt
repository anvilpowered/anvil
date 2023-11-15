package org.anvilpowered.anvil.sponge.user

import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import java.util.UUID

fun ServerPlayer.toAnvilPlayer(): Player = AnvilSpongePlayer(this)

private class AnvilSpongePlayer(
    override val platformDelegate: ServerPlayer,
) : Player,
    Subject by platformDelegate.toAnvilSubject() {
    override val id: UUID = platformDelegate.uniqueId()
}
