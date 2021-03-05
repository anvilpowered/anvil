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

package org.anvilpowered.anvil.sponge7.util

import com.google.inject.Inject
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.UUID
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.common.metric.MetricService
import org.bstats.MetricsBase
import org.bstats.charts.CustomChart
import org.bstats.json.JsonObjectBuilder
import org.slf4j.Logger
import org.spongepowered.api.Platform
import org.spongepowered.api.Sponge
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.scheduler.Task

class Sponge7MetricService @Inject constructor(
  private val logger: Logger
) : MetricService {

  private lateinit var plugin: PluginContainer
  private lateinit var configDir: Path
  private var serviceId = 0
  private lateinit var metricsBase: MetricsBase
  private var serverUUID: String? = null
  private var logErrors = false
  private var logSentData = false
  private var logResponseStatusText = false

  override fun initializeMetric(env: Environment) {
    val serviceId: Int? = env.pluginInfo.metricIds["sponge"]
    checkNotNull(serviceId) { "Could not find a valid Metrics Id for Sponge. Please check your PluginInfo" }
    initialize(env, serviceId)
  }

  private fun initialize(environment: Environment, serviceId: Int) {
    this.serviceId = serviceId
    this.configDir = Sponge.getConfigManager().getSharedConfig(environment.plugin).configPath
    try {
      loadConfig()
    } catch (e: IOException) {
      logger.warn("Failed to load bStats config!", e)
      return
    }
    this.plugin = environment.injector.getInstance(PluginContainer::class.java)
    metricsBase = MetricsBase(
      "sponge",
      serverUUID,
      serviceId,
      Sponge.getMetricsConfigManager().getCollectionState(plugin).asBoolean(),
      { builder: JsonObjectBuilder -> appendPlatformData(builder) },
      { builder: JsonObjectBuilder -> appendServiceData(builder) },
      { task: Runnable ->
        val scheduler: Scheduler = Sponge.getScheduler()
        val taskBuilder: Task.Builder = scheduler.createTaskBuilder()
        taskBuilder.execute(task).submit(plugin)
      },
      { true },
      logger::warn,
      logger::info,
      logErrors,
      logSentData,
      logResponseStatusText
    )
    val builder = StringBuilder()
    builder.append("Plugin ").append(plugin.getName()).append(" is using bStats Metrics ")
    if (Sponge.getMetricsConfigManager().getCollectionState(plugin).asBoolean()) {
      builder.append(" and is allowed to send data.")
    } else {
      builder.append(" but currently has data sending disabled.").append(System.lineSeparator())
      builder.append("To change the enabled/disabled state of any bStats use in a plugin, visit the Sponge config!")
    }
    logger.info(builder.toString())
  }

  private fun loadConfig() {
    val configPath: File = configDir.resolve("bStats").toFile()
    if (!configPath.exists()) {
      if (!configPath.mkdirs()) {
        logger.error("Could not create the bStats directory!")
      }
    }

    val configFile = File(configPath, "config.conf")
    val configurationLoader: HoconConfigurationLoader =
      HoconConfigurationLoader.builder().setFile(configFile).build()

    if (!configFile.exists()) {
      require(configFile.mkdirs()) { "Could not create the bStats config!" }
    }

    val node: CommentedConfigurationNode = configurationLoader.load()
    node.getNode("serverUuid").value = UUID.randomUUID().toString()
    node.getNode("logFailedRequests").value = false
    node.getNode("logSentData").value = false
    node.getNode("logResponseStatusText").value = false
    node.getNode("serverUuid").setComment(
      "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
        "To control whether this is enabled or disabled, see the Sponge configuration file.\n" +
        "Check out https://bStats.org/ to learn more :)"
    )
    node.getNode("configVersion").value = 2
    configurationLoader.save(node)

    serverUUID = node.getNode("serverUuid").string
    logErrors = node.getNode("logFailedRequests").getBoolean(false)
    logSentData = node.getNode("logSentData").getBoolean(false)
    logResponseStatusText = node.getNode("logResponseStatusText").getBoolean(false)
  }

  fun addCustomChart(chart: CustomChart) {
    metricsBase.addCustomChart(chart)
  }

  private fun appendPlatformData(builder: JsonObjectBuilder) {
    builder.appendField("playerAmount", Sponge.getServer().onlinePlayers.size)
    builder.appendField("onlineMode", if (Sponge.getServer().onlineMode) 1 else 0)
    builder.appendField("minecraftVersion", Sponge.getGame().platform.minecraftVersion.name)
    builder.appendField("spongeImplementation", Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).name)
    builder.appendField("javaVersion", System.getProperty("java.version"))
    builder.appendField("osName", System.getProperty("os.name"))
    builder.appendField("osArch", System.getProperty("os.arch"))
    builder.appendField("osVersion", System.getProperty("os.version"))
    builder.appendField("coreCount", Runtime.getRuntime().availableProcessors())
  }

  private fun appendServiceData(builder: JsonObjectBuilder) {
    builder.appendField("pluginVersion", plugin.version.orElse("unknown"))
  }
}
