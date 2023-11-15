package org.anvilpowered.anvil.paper.user

import net.kyori.adventure.audience.Audience
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
}
