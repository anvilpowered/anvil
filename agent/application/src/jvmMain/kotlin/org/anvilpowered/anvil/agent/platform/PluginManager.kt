package org.anvilpowered.anvil.agent.platform

import org.anvilpowered.anvil.platform.AgentPlugin

interface PluginManager {
    val plugins: List<AgentPlugin>

    interface Scope {
        val pluginManager: PluginManager
    }
}
