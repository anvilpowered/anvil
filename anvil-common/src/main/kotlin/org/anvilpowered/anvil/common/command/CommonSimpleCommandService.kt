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
package org.anvilpowered.anvil.common.command

import com.google.inject.Inject
import java.util.Optional
import java.util.function.Predicate
import java.util.function.Supplier
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.util.TextService
import org.checkerframework.checker.nullness.qual.Nullable

abstract class CommonSimpleCommandService<TString, TCommandSource> :
  SimpleCommandService<TString, TCommandSource> {

  @Inject
  protected lateinit var environment: Environment

  @Inject
  protected lateinit var pluginInfo: PluginInfo<TString>

  @Inject
  protected lateinit var textService: TextService<TString, TCommandSource>

  private val noPermission: TString by lazy {
    textService.builder()
      .appendPrefix()
      .red().append("You do not have permission for this command!")
      .build()
  }

  private val successfullyReloaded: TString by lazy {
    textService.builder()
      .appendPrefix()
      .green().append("Successfully reloaded!")
      .build()
  }

  private fun sendSubCommandError(
    source: TCommandSource,
    alias: String?,
    subCommands: List<CommandMapping<SimpleCommand<TString, TCommandSource>>>
  ) {
    textService.builder()
      .red().append("Input command ", alias ?: "null", " was not a valid subcommand!\n")
      .appendIf(alias != null, alias, "\n^")
      .append("\nAvailable: ")
      .append(subCommands.asSequence().map { it.name }.joinToString(", "))
      .sendTo(source)
  }

  protected fun TCommandSource.sendNoPermission() = textService.send(noPermission, this)

  private fun CommandMapping<out SimpleCommand<TString, TCommandSource>>.getFullPath(): String {
    val stack: MutableList<String> = mutableListOf()
    var current = this
    while (true) {
      stack.add(current.parentCommand.orElse(null)?.also { current = it }?.name ?: break)
    }
    val s = StringBuilder(32)
    s.append("/")
    for (name in stack.asReversed()) {
      s.append(name).append(" ")
    }
    s.append(name).append(" ")
    return s.toString()
  }

  private inner class RoutingMapping(
    aliases: List<String>,
    rootCommand: SimpleCommand<TString, TCommandSource>?,
    private val subCommands: List<CommandMapping<SimpleCommand<TString, TCommandSource>>>,
    childCommandFallback: Boolean
  ) : BaseCommandMapping<SimpleCommand<TString, TCommandSource>>(
    aliases,
    RoutingCommand(rootCommand, subCommands, childCommandFallback)
  ) {

    init {
      for (mapping in subCommands) {
        (mapping as? BaseCommandMapping)?.setParentCommand(this)
      }
    }

    override fun getSubCommands(): List<CommandMapping<SimpleCommand<TString, TCommandSource>>> = subCommands
  }

  private inner class RoutingCommand(
    private val root: SimpleCommand<TString, TCommandSource>?,
    private val subCommands: List<CommandMapping<SimpleCommand<TString, TCommandSource>>>,
    private val childCommandFallback: Boolean
  ) : SimpleCommand<TString, TCommandSource> {
    override fun execute(source: TCommandSource, alias: String, context: Array<String>) {
      val length = context.size
      if (length > 0) {
        for (mapping in subCommands) {
          if (context[0] == mapping.name) {
            // remove the first argument (the child selector)
            mapping.command.execute(source, context[0], context.copyOfRange(1, length))
            return
          }
        }
        if (!childCommandFallback) {
          sendSubCommandError(source, context[0], subCommands)
          return
        }
      }
      // reached the end; if no children have matched, run the root command
      if (root == null) {
        sendSubCommandError(source, null, subCommands)
      } else {
        root.execute(source, alias, context)
      }
    }

    override fun suggest(
      source: TCommandSource,
      alias: String,
      context: Array<String>
    ): List<String> {
      return when (val length = context.size) {
        0 -> listOf()
        1 -> subCommands.asSequence().map { it.name }.toList()
        else -> {
          for (mapping in subCommands) {
            if (mapping.name.contains(context[0])) {
              // remove the first argument (the child selector)
              return mapping.command.suggest(source, context[0], context.copyOfRange(1, length))
            }
          }
          if (!childCommandFallback || root == null) {
            listOf()
          } else root.suggest(source, alias, context)
        }
      }
    }
  }

  private inner class RootCommand(
    private val helpUsage: String,
    private val extended: (TCommandSource) -> Boolean,
  ) : SimpleCommand<TString, TCommandSource> {
    override fun execute(source: TCommandSource, alias: String, context: Array<String>) {
      val isExtended = extended(source)
      textService.builder()
        .appendPrefix()
        .aqua().append("Running version ")
        .green().append(pluginInfo.version)
        .aqua().append(" by ")
        .appendJoining(", ", *pluginInfo.authors)
        .append("\n")
        .appendIf(
          isExtended, textService.builder()
            .green().append("Use ")
            .gold().append(helpUsage)
            .green().append(" for help")
        )
        .appendIf(
          !isExtended, textService.builder()
            .red().append("You do not have permission for any sub-commands")
        )
        .sendTo(source)
    }
  }

  private inner class VersionCommand(
    private val helpUsage: String,
    private val extended: (TCommandSource) -> Boolean,
  ) : SimpleCommand<TString, TCommandSource> {
    override fun execute(source: TCommandSource, alias: String, context: Array<String>) {
      val isExtended = extended(source)
      textService.builder()
        .appendPrefix()
        .aqua().append("Running version ")
        .green().append(pluginInfo.version)
        .aqua().append(" by ")
        .appendJoining(", ", *pluginInfo.authors)
        .gray().append("\nBuild date: ")
        .aqua().append(pluginInfo.buildDate, "\n")
        .appendIf(
          isExtended, textService.builder()
            .green().append("Use ")
            .gold().append(helpUsage)
            .green().append(" for help")
        )
        .appendIf(
          !isExtended, textService.builder()
            .red().append("You do not have permission for any sub-commands")
        )
        .sendTo(source)
    }

    override fun getUsage(source: TCommandSource): Optional<TString> {
      return Optional.ofNullable(textService.of(""))
    }

    override fun getShortDescription(source: TCommandSource): Optional<TString> {
      return Optional.ofNullable(textService.of("Anvil version command"))
    }
  }

  private inner class HelpCommand(
    private val subCommandsSupplier: Supplier<List<CommandMapping<out SimpleCommand<TString, TCommandSource>>>>
  ) : SimpleCommand<TString, TCommandSource> {
    override fun execute(source: TCommandSource, alias: String, context: Array<out String>?) {
      val helpList = subCommandsSupplier.get().asSequence()
        .filter { it.command.canExecute(source) }
        .map { mapping ->
          val command = mapping.command
          val fullPath = mapping.getFullPath()
          val otherAliases = mapping.otherAliases
          val builder = textService.builder().gold().append(fullPath)
          command.getShortDescription(source).ifPresent { builder.append("- ", it) }
          command.getLongDescription(source).ifPresent { builder.append("\n", it) }
          builder.gray().append("\nUsage: ", fullPath)
          command.getUsage(source).ifPresent { usage ->
            builder.appendIf(otherAliases.isNotEmpty(), ", ")
              .appendJoining(", ", *otherAliases.toTypedArray())
              //Only append a space before the usage if the aliases are empty
              .appendIf(otherAliases.isNotEmpty(), " ")
              .append(usage)
          }
          builder.build()
        }.toList()
      textService.paginationBuilder()
        .title(textService.builder().gold().append(pluginInfo.name, " - ", pluginInfo.organizationName).build())
        .padding(textService.builder().dark_green().append("-").build())
        .contents(helpList)
        .build()
        .sendTo(source)
    }

    override fun getShortDescription(source: TCommandSource): Optional<TString> {
      return Optional.ofNullable(textService.of("Help command"))
    }
  }

  private inner class ReloadCommand : SimpleCommand<TString, TCommandSource> {
    override fun execute(source: TCommandSource, alias: String, context: Array<String>) {
      environment.reload()
      textService.send(successfullyReloaded, source)
    }
  }

  override fun mapRouting(
    aliases: List<String>,
    rootCommand: @Nullable SimpleCommand<TString, TCommandSource>?,
    subCommands: List<CommandMapping<SimpleCommand<TString, TCommandSource>>>,
    childCommandFallback: Boolean,
  ): CommandMapping<SimpleCommand<TString, TCommandSource>>? {
    return RoutingMapping(aliases, rootCommand, subCommands, childCommandFallback)
  }

  override fun mapTerminal(
    aliases: List<String>,
    command: SimpleCommand<TString, TCommandSource>
  ): CommandMapping<SimpleCommand<TString, TCommandSource>> {
    return BaseCommandMapping(aliases, command)
  }

  override fun generateRoot(
    helpUsage: String,
    extended: Predicate<TCommandSource>,
  ): SimpleCommand<TString, TCommandSource> {
    return RootCommand(helpUsage, extended::test)
  }

  override fun generateVersion(
    helpUsage: String,
    extended: Predicate<TCommandSource>,
  ): SimpleCommand<TString, TCommandSource> {
    return VersionCommand(helpUsage, extended::test)
  }

  override fun generateHelp(
    treeCommandSupplier: Supplier<List<CommandMapping<out SimpleCommand<TString, TCommandSource>>>>,
  ): SimpleCommand<TString, TCommandSource>? {
    return HelpCommand(treeCommandSupplier)
  }

  override fun generateReload(): SimpleCommand<TString, TCommandSource> {
    return ReloadCommand()
  }
}
