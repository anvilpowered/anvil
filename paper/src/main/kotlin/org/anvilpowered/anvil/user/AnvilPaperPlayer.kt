package org.anvilpowered.anvil.user

import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import java.util.UUID
import org.bukkit.entity.Player as PaperPlayer

fun PaperPlayer.toAnvilPlayer(): Player = AnvilPaperPlayer(this)

private class AnvilPaperPlayer(
    val paperPlayer: PaperPlayer,
) : Player,
    Audience by paperPlayer,
    Subject by paperPlayer.toAnvilSubject() {
    override val id: UUID = paperPlayer.uniqueId
}
