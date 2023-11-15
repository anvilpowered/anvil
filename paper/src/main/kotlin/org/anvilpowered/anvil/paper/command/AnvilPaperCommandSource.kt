package org.anvilpowered.anvil.paper.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.anvil.core.user.Player
import org.anvilpowered.anvil.core.user.Subject
import org.anvilpowered.anvil.paper.user.toAnvilPlayer
import org.anvilpowered.anvil.paper.user.toAnvilSubject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player as PaperPlayer

fun CommandSender.toAnvilCommandSource(): CommandSource = AnvilPaperCommandSource(this)

@Suppress("UnstableApiUsage")
fun CommandSourceStack.toAnvilCommandSource(): CommandSource = AnvilPaperCommandSource(sender)

private class AnvilPaperCommandSource(
    override val platformDelegate: CommandSender,
) : CommandSource {
    override val audience: Audience = platformDelegate
    override val subject: Subject = platformDelegate.toAnvilSubject()
    override val player: Player? = (platformDelegate as? PaperPlayer)?.toAnvilPlayer()
}
