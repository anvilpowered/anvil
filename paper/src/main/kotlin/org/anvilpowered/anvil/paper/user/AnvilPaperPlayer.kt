package org.anvilpowered.anvil.paper.user

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import java.util.UUID
import org.bukkit.entity.Player as PaperPlayer

fun PaperPlayer.toAnvilPlayer(): Player = AnvilPaperPlayer(this)

private class AnvilPaperPlayer(
    override val platformDelegate: PaperPlayer,
) : Player,
    Audience by platformDelegate,
    Subject by platformDelegate.toAnvilSubject() {
    override val id: UUID = platformDelegate.uniqueId
    override val username: String = platformDelegate.name
    override val displayname: Component = platformDelegate.displayName()
    override val latencyMs: Int
        get() = platformDelegate.ping
}
