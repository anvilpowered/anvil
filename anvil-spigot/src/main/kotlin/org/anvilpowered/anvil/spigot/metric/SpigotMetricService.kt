/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.spigot.metric

import com.google.inject.Inject
import java.io.File
import java.io.IOException
import java.util.UUID
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.anvil.common.metric.MetricService
import org.bstats.MetricsBase
import org.bstats.json.JsonObjectBuilder
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.slf4j.Logger

class SpigotMetricService @Inject constructor(
  private val logger: Logger,
  private val userService: UserService<Player, Player>
) : MetricService {

  private lateinit var environment: Environment
  private var metricsBase: MetricsBase? = null

  override fun initialize(env: Environment) {
    val serviceId: Int? = env.pluginInfo.metricIds["spigot"]
    checkNotNull(serviceId) { "Could not find a valid Metrics Id for Spigot. Please check your PluginInfo" }
    initialize(env, serviceId)
  }

  private fun initialize(environment: Environment, serviceId: Int) {
    this.environment = environment
    val bStatsFolder = File("plugins/bStats")
    val configFile = File(bStatsFolder, "config.yml")
    if (!bStatsFolder.exists()) {
      require(configFile.mkdirs()) { "Could not create the bStats config!" }
    }
    val config: YamlConfiguration = YamlConfiguration.loadConfiguration(configFile)
    if (!config.isSet("serverUuid")) {
      config.addDefault("enabled", true)
      config.addDefault("serverUuid", UUID.randomUUID().toString())
      config.addDefault("logFailedRequests", false)
      config.addDefault("logSentData", false)
      config.addDefault("logResponseStatusText", false)

      config.options().header(
        """
          bStats collects some data for plugin authors like how many servers are using their plugins.\n
          To honor their work, you should not disable it.\n
          This has nearly no effect on the server performance!\n
          Check out https://bStats.org/ to learn more :)
          """.trimIndent()
      ).copyDefaults(true)
      try {
        config.save(configFile)
      } catch (ignored: IOException) {
      }
    }

    val enabled: Boolean = config.getBoolean("enabled", true)
    val serverUUID: String = config.getString("serverUuid")!!
    val logErrors: Boolean = config.getBoolean("logFailedRequests", false)
    val logSentData: Boolean = config.getBoolean("logSentData", false)
    val logResponseStatusText: Boolean = config.getBoolean("logResponseStatusText", false)
    metricsBase = MetricsBase(
      "bukkit",
      serverUUID,
      serviceId,
      enabled,
      { appendPlatformData(it) },
      { appendServiceData(it) },
      { Bukkit.getScheduler().runTask(environment.plugin as Plugin, it) },
      { (environment.plugin as Plugin).isEnabled },
      { message: String, error: Throwable -> logger.error(message, error) },
      { logger.info(it) },
      logErrors,
      logSentData,
      logResponseStatusText
    )
  }

  private fun appendPlatformData(builder: JsonObjectBuilder) {
    builder.appendField("playerAmount", getPlayerAmount())
    builder.appendField("onlineMode", if (Bukkit.getOnlineMode()) 1 else 0)
    builder.appendField("bukkitVersion", Bukkit.getVersion())
    builder.appendField("bukkitName", Bukkit.getName())
    builder.appendField("javaVersion", System.getProperty("java.version"))
    builder.appendField("osName", System.getProperty("os.name"))
    builder.appendField("osArch", System.getProperty("os.arch"))
    builder.appendField("osVersion", System.getProperty("os.version"))
    builder.appendField("coreCount", Runtime.getRuntime().availableProcessors())
  }

  private fun appendServiceData(builder: JsonObjectBuilder) = builder.appendField("pluginVersion", environment.pluginInfo.version)
  private fun getPlayerAmount(): Int = userService.onlinePlayers.size
}
