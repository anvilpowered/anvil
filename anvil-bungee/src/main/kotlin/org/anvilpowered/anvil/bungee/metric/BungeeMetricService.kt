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

package org.anvilpowered.anvil.bungee.metric

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Inject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HttpsURLConnection
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.common.metric.MetricService
import org.slf4j.Logger

class BungeeMetricService @Inject constructor(
  private val logger: Logger
) : MetricService {

  private lateinit var environment: Environment
  private var serviceId = 0
  private var enabled = false
  private var serverUUID: String? = null
  private var logFailedRequests = false
  private var logSentData = false
  private var logResponseStatusText = false
  private val knownMetricsInstances: MutableList<Any> = ArrayList()

  override fun initialize(env: Environment) {
    val bungeeId: Int? = env.pluginInfo.metricIds["bungeecord"]
    requireNotNull(bungeeId) { "Could not find a valid Metrics Id for BungeeCord. Please check your PluginInfo" }
    initialize(env, bungeeId)
  }

  private fun initialize(environment: Environment, serviceId: Int) {
    this.environment = environment
    this.serviceId = serviceId
    try {
      loadConfig()
    } catch (e: IOException) {
      logger.error("Failed to load bStats config!", e)
      return
    }

    if (!enabled) {
      return
    }

    val usedMetricsClass = getFirstBStatsClass() ?: return
    if (usedMetricsClass == javaClass) {
      linkMetrics(this)
      startSubmitting()
    } else {
      val logMsg = "Failed to link to first metrics class ${usedMetricsClass.name}"
      try {
        usedMetricsClass.getMethod("linkMetrics", Any::class.java).invoke(null, this)
      } catch (e: NoSuchMethodException) {
        if (logFailedRequests) {
          logger.error(logMsg, e)
        }
      } catch (e: IllegalAccessException) {
        if (logFailedRequests) {
          logger.error(logMsg, e)
        }
      } catch (e: InvocationTargetException) {
        if (logFailedRequests) {
          logger.error(logMsg, e)
        }
      }
    }
  }

  private fun linkMetrics(metrics: Any) = knownMetricsInstances.add(metrics)

  private fun startSubmitting() {
    val initialDelay = (1000 * 60 * (3 + Math.random() * 3)).toLong()
    val secondDelay = (1000 * 60 * (Math.random() * 30)).toLong()
    val plugin = environment.plugin as Plugin
    ProxyServer.getInstance().scheduler.schedule(plugin, { submitData() }, initialDelay, TimeUnit.MILLISECONDS)
    ProxyServer.getInstance().scheduler.schedule(
      plugin, { submitData() }, initialDelay + secondDelay, 1000 * 60 * 30, TimeUnit.MILLISECONDS
    )
  }

  private fun getServerData(): JsonObject {
    val proxyInstance = ProxyServer.getInstance()
    val data = JsonObject()
    data.addProperty("serverUUID", serverUUID)
    data.addProperty("playerAmount", proxyInstance.onlineCount)
    data.addProperty("managedServers", proxyInstance.servers.size)
    data.addProperty("onlineMode", if (proxyInstance.config.isOnlineMode) 1 else 0)
    data.addProperty("bungeecordVersion", proxyInstance.version)
    data.addProperty("javaVersion", System.getProperty("java.version"))
    data.addProperty("osName", System.getProperty("os.name"))
    data.addProperty("osArch", System.getProperty("os.arch"))
    data.addProperty("osVersion", System.getProperty("os.version"))
    data.addProperty("coreCount", Runtime.getRuntime().availableProcessors())
    return data
  }

  private fun submitData() {
    val data: JsonObject = getServerData()
    val pluginData = JsonArray()
    for (metrics in knownMetricsInstances) {
      try {
        val plugin = metrics.javaClass.getMethod("getPluginData").invoke(metrics)
        if (plugin is JsonObject) {
          pluginData.add(plugin)
        }
      } catch (ignored: Exception) {
      }
    }
    data.add("plugins", pluginData)
    try {
      sendData(data)
    } catch (e: Exception) {
      if (logFailedRequests) {
        logger.error("Could not submit plugin stats!", e)
      }
    }
  }

  @Throws(IOException::class)
  private fun loadConfig() {
    val bStatsFolder = File("plugins/bStats")
    check(bStatsFolder.mkdirs()) { "Could not create the config directory for bStats!" }
    val configFile = File(bStatsFolder, "config.yml")
    check(configFile.mkdirs()) { "Could not create the config file for bStats!" }

    writeFile(
      configFile,
      "#bStats collects some data for plugin authors like how many servers are using their plugins.",
      "#To honor their work, you should not disable it.",
      "#This has nearly no effect on the server performance!",
      "#Check out https://bStats.org/ to learn more :)",
      "enabled: true",
      "serverUuid: \"" + UUID.randomUUID() + "\"",
      "logFailedRequests: false",
      "logSentData: false",
      "logResponseStatusText: false"
    )

    val configuration: Configuration = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)

    enabled = configuration.getBoolean("enabled", true)
    serverUUID = configuration.getString("serverUuid")
    logFailedRequests = configuration.getBoolean("logFailedRequests", false)
    logSentData = configuration.getBoolean("logSentData", false)
    logResponseStatusText = configuration.getBoolean("logResponseStatusText", false)
  }

  private fun getFirstBStatsClass(): Class<*>? {
    val bStatsFolder = File("plugins/bStats")
    bStatsFolder.mkdirs()
    check(bStatsFolder.mkdirs()) { "Could not create the bStats config folder!" }
    val tempFile = File(bStatsFolder, "temp.txt")
    return try {
      val className = readFile(tempFile)
      if (className != null) {
        try {
          return Class.forName(className)
        } catch (ignored: ClassNotFoundException) {
        }
      }
      writeFile(tempFile, javaClass.name)
      javaClass
    } catch (e: IOException) {
      if (logFailedRequests) {
        logger.error("Failed to get first bStats class!", e)
      }
      null
    }
  }

  @Throws(IOException::class)
  private fun readFile(file: File): String? {
    return if (!file.exists()) {
      null
    } else {
      BufferedReader(FileReader(file)).use { it.readLine() }
    }
  }

  @Throws(IOException::class)
  private fun writeFile(file: File, vararg lines: String) {
    BufferedWriter(FileWriter(file)).use {
      for (line in lines) {
        it.write(line)
        it.newLine()
      }
    }
  }

  private fun sendData(data: JsonObject?) {
    requireNotNull(data) { "Data cannot be null" }
    if (logSentData) {
      logger.info("Sending data to bStats: $data")
    }
    val connection: HttpsURLConnection = URL("https://bStats.org/submitData/bungeecord").openConnection() as HttpsURLConnection
    val compressedData = compress(data.toString())

    connection.requestMethod = "POST"
    connection.addRequestProperty("Accept", "application/json")
    connection.addRequestProperty("Connection", "close")
    connection.addRequestProperty("Content-Encoding", "gzip")
    connection.addRequestProperty("Content-Length", compressedData!!.size.toString())
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("User-Agent", "MC-Server/1")

    connection.doOutput = true
    DataOutputStream(connection.outputStream).use { outputStream -> outputStream.write(compressedData) }
    val builder = StringBuilder()
    BufferedReader(InputStreamReader(connection.inputStream)).use { bufferedReader ->
      var line: String?
      while (bufferedReader.readLine().also { line = it } != null) {
        builder.append(line)
      }
    }
    if (logResponseStatusText) {
      logger.info("Sent data to bStats and received response: $builder")
    }
  }

  private fun compress(str: String?): ByteArray? {
    if (str == null) {
      return null
    }
    val outputStream = ByteArrayOutputStream()
    GZIPOutputStream(outputStream).use { gzip -> gzip.write(str.toByteArray(StandardCharsets.UTF_8)) }
    return outputStream.toByteArray()
  }
}
