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

package org.anvilpowered.anvil.common.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.inject.Inject
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.GlobalScope
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.InfoDumpService
import org.anvilpowered.anvil.api.util.TextService
import org.slf4j.Logger

class CommonInfoDumpService<TCommandSource> : InfoDumpService<TCommandSource> {

  @Inject
  private lateinit var logger: Logger

  @Inject
  private lateinit var textService: TextService<TCommandSource>

  private val gson = GsonBuilder()
    .setPrettyPrinting()
    .excludeFieldsWithoutExposeAnnotation()
    .setLenient()
    .create()

  private fun collectInformation(environment: Environment): JsonElement {
    val result = JsonObject()
    val pluginInfo = JsonObject()
    val info = environment.injector.getInstance(PluginInfo::class.java)
    pluginInfo.addProperty("version", info.version)
    pluginInfo.addProperty("description", info.description)
    pluginInfo.addProperty("url", info.url)
    pluginInfo.addProperty("authors", info.authors.joinToString(", "))
    pluginInfo.addProperty("buildDate", info.buildDate)
    result.add("pluginInfo", pluginInfo)
    val keys = JsonObject()
    for (it in Keys.getAll(environment.name)) {
      if (it.value.isSensitive) {
        keys.addProperty(it.key, "***")
      } else {
        keys.addProperty(it.key, toString(it.value, environment.registry))
      }
    }
    result.add("keys", keys)
    return result
  }

  private fun collectSystemInfo(): JsonElement {
    val sysInfo = JsonObject()
    sysInfo.addProperty("os", System.getProperty("os.name"))
    sysInfo.addProperty("osVersion", System.getProperty("os.version"))
    sysInfo.addProperty("architecture", System.getProperty("os.arch"))
    sysInfo.addProperty("javaVersion", System.getProperty("java.version"))
    sysInfo.addProperty("javaVendor", System.getProperty("java.vendor"))
    sysInfo.addProperty("platform", Anvil.getPlatform().name)
    return sysInfo
  }

  override suspend fun publishInfo(source: TCommandSource) {
    val data = JsonObject()
    val plugins = JsonObject()
    data.add("system", collectSystemInfo())
    plugins.add("anvil", collectInformation(Anvil.getEnvironment()))
    for (env in Anvil.getEnvironmentManager().environments.values) {
      if (env.name == "anvil") {
        continue
      }
      plugins.add(env.name, collectInformation(env))
    }
    data.add("plugins", plugins)
    publish(source, data.asJsonObject)
  }

  override suspend fun publishInfo(source: TCommandSource, vararg environments: Environment) {
    val data = JsonObject()
    val plugins = JsonObject()
    data.add("system", collectSystemInfo())
    // Always add the Anvil environment, regardless of whether it is in the provided environment vararg
    plugins.add("anvil", collectInformation(Anvil.getEnvironment()))
    for (env in environments) {
      if (env.name == "anvil") {
        continue
      }
      plugins.add(env.name, collectInformation(env))
    }
    data.add("plugins", plugins)
    publish(source, data.asJsonObject)
  }

  private suspend fun publish(source: TCommandSource, data: JsonObject) {
    val client = HttpClient(CIO)
    val readable = GsonBuilder()
      .setPrettyPrinting()
      .create().toJson(data)
    val requestBuilder = HttpRequestBuilder()
    requestBuilder.url("http://dump.anvilpowered.org/dump")
    requestBuilder.contentType(ContentType.Text.Plain)
    requestBuilder.body = readable.toByteArray(Charsets.UTF_8)
    GlobalScope.runCatching {
      val resp = client.post<HttpResponse>(requestBuilder)
      if (resp.status.value != 200) {
        logger.error(
          """
              An error occurred while attempting to post your dump.
              The server may be down at this time or there is an issue with your internet connection.
              If you believe this may be a bug, please contact the Anvil discord server.
          """.trimIndent())
        return@runCatching
      }
      val key = parse(resp.content.readUTF8Line())
      check(key.has("key"))
      val url = "http://dump.anvilpowered.org/${key.get("key").asString}.json"
      textService.builder
        .appendPrefix()
        .green().append("If a developer has requested you run this command, please provide them with the following link\n")
        .gold().append(url).onClickOpenUrl(url)
        .sendTo(source)
    }.getOrElse { it.printStackTrace() }
  }

  private fun parse(string: String?): JsonObject {
    check(string != null) { "An error occurred while posting the data" }
    return gson.fromJson(string, JsonObject::class.java)
  }

  fun <T> toString(key: Key<T>, registry: Registry): String = key.toString(registry.getOrDefault(key))
}
