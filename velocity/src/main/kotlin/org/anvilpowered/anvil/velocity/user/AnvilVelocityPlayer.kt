package org.anvilpowered.anvil.velocity.user

import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import java.util.UUID
import com.velocitypowered.api.proxy.Player as VelocityPlayer

fun VelocityPlayer.toAnvilPlayer(): Player = AnvilVelocityPlayer(this)

private class AnvilVelocityPlayer(
    val velocityPlayer: VelocityPlayer,
) : Player,
    Audience by velocityPlayer,
    Subject by velocityPlayer.toAnvilSubject() {
    override val id: UUID = velocityPlayer.uniqueId
}
