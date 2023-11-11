package org.anvilpowered.anvil.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
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
}
