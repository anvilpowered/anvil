package org.anvilpowered.anvil.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.anvilpowered.anvil.domain.command.CommandSource
import org.anvilpowered.anvil.domain.user.Audience
import org.anvilpowered.anvil.domain.user.GameUser
import org.anvilpowered.anvil.domain.user.Player
import org.anvilpowered.anvil.domain.user.Subject
import org.anvilpowered.anvil.domain.user.User
import org.anvilpowered.anvil.user.toAnvilPlayer
import org.anvilpowered.anvil.user.toAnvilSubject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player as PaperPlayer

fun CommandSender.toAnvilCommandSource(): CommandSource = AnvilPaperCommandSource(this)

@Suppress("UnstableApiUsage")
fun CommandSourceStack.toAnvilCommandSource(): CommandSource = AnvilPaperCommandSource(sender)

private class AnvilPaperCommandSource(
    paperCommandSource: CommandSender,
) : CommandSource {
    override val audience: Audience = paperCommandSource
    override val subject: Subject = paperCommandSource.toAnvilSubject()
    override val player: Player? = (paperCommandSource as? PaperPlayer)?.toAnvilPlayer()
    override val gameUser: GameUser? = player?.gameUser

    override suspend fun getUserOrNull(): User? {
        TODO("Not yet implemented")
    }
}
