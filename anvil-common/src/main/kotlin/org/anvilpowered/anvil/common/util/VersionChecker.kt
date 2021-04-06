package org.anvilpowered.anvil.common.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.util.TextService
import java.net.URL
import java.util.Scanner

@Singleton
class VersionChecker @Inject constructor(
  private val pluginInfo: PluginInfo,
  private val textService: TextService<*>
) {

  private var url = ""
  private var currentVersion = ""

  private val client = HttpClient(CIO)
  private val request = HttpRequestBuilder()

  fun checkVersion() {
    val specified = pluginInfo.sourceUrl
    currentVersion = pluginInfo.version
    if (currentVersion.isEmpty()) {
      return
    }
    if (currentVersion.contains("-SNAPSHOT")) {
      textService.builder()
        .appendPrefix()
        .red().append("You are currently running a development build! AnvilPowered does not recommend running development builds in production environments!")
        .sendToConsole()
      return
    }
    when {
      specified.contains("github") -> {
        val split = specified.substring(19, specified.length).split(Regex("/"))
        url = "https://api.github.com/repos/${split[0]}/${split[1]}/releases/latest"
        checkGithub()
      }
      specified.contains("spigot") -> {
        val resource = specified.substring(specified.indexOfLast { it == '.' } + 1, specified.length - 1)
        url = "https://api.spigotmc.org/legacy/update.php?resource=$resource"
        checkSpigot()
      }
    }
  }

  private fun checkGithub() {
    request.url(url)
    GlobalScope.launch {
      val response = client.request<HttpResponse>(request)
      val gson = Gson().fromJson(response.content.readUTF8Line(), JsonObject::class.java)
      check(gson.has("tag_name")) { "An error occurred while finding the latest release from GitHub!" }
      var latest = gson.get("tag_name").asString
      latest = latest.replace(Regex("[a-zA-Z]"), "")
      compareVersions(latest)
    }
  }

  private fun checkSpigot() {
    try {
      val scanner = Scanner(URL(url).openStream())
      if (scanner.hasNext()) {
        compareVersions(scanner.next())
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun compareVersions(latest: String) {
    val split = latest.split(Regex("\\."))
    val lMaj = split[0].toInt()
    val lMin = split[1].toInt()
    val lBug = split[2].toInt()

    val splitCurrent = currentVersion.split(Regex("\\."))
    val cMaj = splitCurrent[0].toInt()
    val cMin = splitCurrent[1].toInt()
    val cBug = splitCurrent[2].toInt()

    if (lMaj > cMaj
      || lMin > cMin
      || lBug > cBug
    ) {
      textService.builder()
        .appendPrefix()
        .red().append("You are running an outdated version of ${pluginInfo.name}, please update to ")
        .gold().append("[$latest]")
        .red().append("! (installed: $currentVersion)")
        .sendToConsole()
    }
  }
}
