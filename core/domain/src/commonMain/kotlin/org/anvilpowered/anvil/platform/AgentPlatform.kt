package org.anvilpowered.anvil.platform

interface AgentPlatform {
    val name: String
    val gameVersion: String
    val platformVersion: String
    val isProxy: Boolean
    val plugins: List<AgentPlugin>
}
