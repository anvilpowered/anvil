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

package org.anvilpowered.anvil.velocity.metric

import com.google.inject.Inject
import com.velocitypowered.api.proxy.ProxyServer
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional
import java.util.UUID
import java.util.regex.Pattern
import java.util.stream.Collectors
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.Platform
import org.anvilpowered.anvil.api.server.LocationService
import org.anvilpowered.anvil.common.metric.MetricService
import org.bstats.MetricsBase
import org.bstats.json.JsonObjectBuilder
import org.slf4j.Logger

class VelocityMetricService @Inject constructor(
  private val logger: Logger,
  private val platform: Platform,
  private val proxyServer: ProxyServer,
  private val locationService: LocationService,
) : MetricService {

  private lateinit var metricsBase: MetricsBase
  private var serverUUID: String? = null
  private var enabled: Boolean = false
  private var logErrors: Boolean = false
  private var logSentData: Boolean = false
  private var logResponseStatusText: Boolean = false
  private lateinit var dataDirectory: Path
  private lateinit var environment: Environment

  override fun initialize(env: Environment) {
    val serviceId: Int? = env.pluginInfo.metricIds["velocity"]
    requireNotNull(serviceId) { "Could not find a valid Metrics Id for Velocity. Please check your PluginInfo" }
    initialize(env, Paths.get("plugins/bStats"), serviceId)
  }

  private fun initialize(environment: Environment, dataPath: Path, serviceId: Int) {
    this.dataDirectory = dataPath
    this.environment = environment
    try {
      setupConfig(true)
    } catch (e: IOException) {
      logger.error("Failed to create bStats config", e)
    }
    metricsBase = MetricsBase(
      platform.name,
      serverUUID,
      serviceId,
      enabled,
      this::appendPlatformData,
      this::appendServiceData,
      { task -> proxyServer.scheduler.buildTask(environment.plugin, task).schedule() },
      { true },
      logger::warn,
      logger::info,
      logErrors,
      logSentData,
      logResponseStatusText
    )
  }

  private fun setupConfig(recreateWhenMalformed: Boolean) {
    val configDir = dataDirectory.parent.resolve("bStats").toFile()
    if (!configDir.exists()) {
      require(configDir.mkdirs()) {"Could not create the bStats config!"}
    }
    val configFile = File(configDir, "config.txt")
    if (!configFile.exists()) {
      writeConfig(configFile)
    }

    val lines: List<String> = readFile(configFile)

    enabled = getConfigValue("enabled", lines).map { anObject: String -> "true" == anObject }.orElse(true)
    serverUUID = getConfigValue("server-uuid", lines).orElse(null)
    logErrors = getConfigValue("log-errors", lines).map { anObject: String -> "true" == anObject }.orElse(false)
    logSentData = getConfigValue("log-sent-data", lines).map { anObject: String -> "true" == anObject }.orElse(false)
    logResponseStatusText = getConfigValue("log-response-status-text", lines)
      .map { anObject: String -> "true" == anObject }.orElse(false)

    if (serverUUID == null) {
      if (recreateWhenMalformed) {
        logger.info("Found malformed bStats config file. Re-creating it...")
        if (!configFile.delete()) {
          logger.error("Could not delete the bStats config!")
          return
        }
        setupConfig(false)
      } else {
        logger.error("Failed to re-create malformed bStats config file")
        return
      }
    }
  }

  private fun writeConfig(file: File) {
    val configContent: MutableList<String> = ArrayList()
    configContent.add("# bStats collects some basic information for plugin authors, like how many people use")
    configContent.add("# their plugin and their total player count. It's recommend to keep bStats enabled, but")
    configContent.add("# if you're not comfortable with this, you can turn this setting off. There is no")
    configContent.add("# performance penalty associated with having metrics enabled, and data sent to bStats")
    configContent.add("# can't identify your server.")
    configContent.add("enabled=true")
    configContent.add("server-uuid=" + UUID.randomUUID().toString())
    configContent.add("log-errors=false")
    configContent.add("log-sent-data=false")
    configContent.add("log-response-status-text=false")
    writeFile(file, configContent)
  }

  private fun getConfigValue(key: String, lines: List<String>): Optional<String> {
    return lines.stream()
      .filter { it.startsWith("$key=") }
      .map { it.replaceFirst(Pattern.quote("$key=").toRegex(), "") }
      .findFirst()
  }

  private fun readFile(file: File): List<String> {
    if (!file.exists()) {
      return emptyList()
    }
    FileReader(file).use { BufferedReader(it).use { o -> return o.lines().collect(Collectors.toList()) } }
  }

  private fun writeFile(file: File, lines: List<String>) {
    if (!file.exists()) {
      if (!file.createNewFile()) {
        logger.error("Could not create the config file for bStats!")
        return
      }
    }
    FileWriter(file).use {
      BufferedWriter(it).use { o ->
        for (line in lines) {
          o.write(line)
          o.newLine()
        }
      }
    }
  }

  private fun appendPlatformData(builder: JsonObjectBuilder) {
    builder.appendField("playerAmount", proxyServer.playerCount)
    builder.appendField("managedServers", locationService.getServers().size)
    builder.appendField("onlineMode", if (proxyServer.configuration.isOnlineMode) 1 else 0)
    builder.appendField("velocityVersionVersion", proxyServer.version.version)
    builder.appendField("velocityVersionName", proxyServer.version.name)
    builder.appendField("velocityVersionVendor", proxyServer.version.vendor)
    builder.appendField("javaVersion", System.getProperty("java.version"))
    builder.appendField("osName", System.getProperty("os.name"))
    builder.appendField("osArch", System.getProperty("os.arch"))
    builder.appendField("osVersion", System.getProperty("os.version"))
    builder.appendField("coreCount", Runtime.getRuntime().availableProcessors())
  }

  private fun appendServiceData(builder: JsonObjectBuilder) =
    builder.appendField("pluginVersion", environment.pluginInfo.version)
}
