package org.anvilpowered.anvil.paper.command

import com.google.inject.Inject
import org.anvilpowered.anvil.api.AnvilImpl
import org.anvilpowered.anvil.api.command.CommandExecuteService
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class PaperCommandExecuteService : CommandExecuteService {

    @Inject(optional = true)
    private val plugin: Plugin? = null

    private fun executeDirect(command: String) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
    }

    override fun execute(command: String) {
        if (Bukkit.isPrimaryThread()) {
            executeDirect(command)
        } else if (plugin != null) {
            Bukkit.getScheduler().runTask(plugin, Runnable { executeDirect(command) })
        } else {
            AnvilImpl.getLogger()
                .error("You must bind org.bukkit.plugin.Plugin to your plugin instance to be able to run commands async")
        }
    }
}
