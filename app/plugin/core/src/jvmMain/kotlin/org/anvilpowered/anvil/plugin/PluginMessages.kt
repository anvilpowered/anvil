package org.anvilpowered.anvil.plugin

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object PluginMessages {
    val pluginPrefix = Component.text("[Anvil Agent] ").color(NamedTextColor.GOLD)
    val notEnoughArgs = Component.text("Not enough arguments!").color(NamedTextColor.RED)
    val noPermission = Component.text("Insufficient Permissions!").color(NamedTextColor.RED)
}
