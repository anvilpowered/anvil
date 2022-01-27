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
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.feature.pagination.Pagination
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.environment.Environment
import org.anvilpowered.anvil.api.append
import org.anvilpowered.anvil.api.appendIf
import org.anvilpowered.anvil.api.appendJoining
import org.anvilpowered.anvil.api.aqua
import org.anvilpowered.anvil.api.command.CommandContext
import org.anvilpowered.anvil.api.command.CommandMapping
import org.anvilpowered.anvil.api.command.SimpleCommand
import org.anvilpowered.anvil.api.command.SimpleCommandService
import org.anvilpowered.anvil.api.gold
import org.anvilpowered.anvil.api.gray
import org.anvilpowered.anvil.api.green
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.red
import org.anvilpowered.anvil.api.sendTo
import java.util.function.Predicate
import java.util.function.Supplier

abstract class CommonSimpleCommandService<TCommandSource> : SimpleCommandService<TCommandSource> {

    @Inject
    lateinit var environment: Environment

    @Inject
    lateinit var pluginInfo: PluginInfo

    private val HELP_DESCRIPTION by lazy { Component.text("help command") }
    private val VERSION_DESCRIPTION by lazy { Component.text("Anvil version command") }

    private val noPermission by lazy {
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text("You do not have permission for this command!").color(NamedTextColor.RED))
            .build()
    }

    private val successfullyReloaded by lazy {
        Component.text()
            .append(pluginInfo.prefix)
            .append(Component.text("Successfully reloaded!").color(NamedTextColor.GREEN))
            .build()
    }

    private fun sendSubCommandError(
        source: TCommandSource,
        alias: String?,
        subCommands: List<CommandMapping<SimpleCommand<TCommandSource>>>,
    ) {
        val fAlias = alias ?: "null"
        Component.text()
            .append(Component.text().append(Component.text("Input command $fAlias was not a valid subcommand!\n")).red().build())
            .append(Component.text("\nAvailable: "))
            .append(Component.text(subCommands.asSequence().map { it.name }.joinToString { ", " }))
            .sendTo(source)
    }

    protected fun TCommandSource.sendNoPermission() = noPermission.sendTo(this)

    private fun CommandMapping<out SimpleCommand<TCommandSource>>.getFullPath(): String {
        val stack: MutableList<String> = mutableListOf()
        var current = this
        while (true) {
            stack.add(current.parentCommand?.also { current = it }?.name ?: break)
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
        rootCommand: SimpleCommand<TCommandSource>?,
        override val subCommands: List<CommandMapping<SimpleCommand<TCommandSource>>>,
        childCommandFallback: Boolean,
    ) : BaseCommandMapping<SimpleCommand<TCommandSource>>(
        aliases,
        RoutingCommand(rootCommand, subCommands, childCommandFallback)
    ) {

        init {
            for (mapping in subCommands) {
                (mapping as? BaseCommandMapping)?.setParentCommand(this)
            }
        }
    }

    private inner class RoutingCommand(
        private val root: SimpleCommand<TCommandSource>?,
        private val subCommands: List<CommandMapping<SimpleCommand<TCommandSource>>>,
        private val childCommandFallback: Boolean,
    ) : SimpleCommand<TCommandSource> {
        override fun execute(context: CommandContext<TCommandSource>) {
            val length = context.arguments.size
            if (length > 0) {
                for (mapping in subCommands) {
                    if (context.arguments[0] == mapping.name) {
                        context.arguments = context.arguments.copyOfRange(1, length)
                        // remove the first argument (the child selector)
                        mapping.command.execute(context)
                        return
                    }
                }
                if (!childCommandFallback) {
                    sendSubCommandError(context.source, context.arguments[0], subCommands)
                    return
                }
            }
            // reached the end; if no children have matched, run the root command
            if (root == null) {
                sendSubCommandError(context.source, null, subCommands)
            } else {
                root.execute(context)
            }
        }

        override fun suggest(context: CommandContext<TCommandSource>): List<String> {
            return when (val length = context.arguments.size) {
                0 -> mutableListOf()
                1 -> subCommands.asSequence().map { it.name }.toMutableList()
                else -> {
                    for (mapping in subCommands) {
                        if (mapping.name.contains(context.arguments[0])) {
                            // remove the first argument (the child selector)
                            context.arguments = context.arguments.copyOfRange(1, length)
                            return mapping.command.suggest(context)
                        }
                    }
                    if (!childCommandFallback || root == null) {
                        listOf()
                    } else root.suggest(context)
                }
            }
        }
    }

    private inner class RootCommand(
        private val helpUsage: String,
        private val extended: (TCommandSource) -> Boolean,
    ) : SimpleCommand<TCommandSource> {
        override fun execute(context: CommandContext<TCommandSource>) {
            val isExtended = extended(context.source)
            Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text().append(Component.text(pluginInfo.version)).green())
                .append(Component.text().append(Component.text(" by ")).aqua())
                .appendJoining(", ", *pluginInfo.authors)
                .append("\n")
                .appendIf(isExtended, Component.text()
                    .append(Component.text().append(Component.text("Use ")).green())
                    .append(Component.text().append(Component.text(helpUsage)).gold())
                    .append(Component.text().append(Component.text(" for help")).green())
                    .build())
                .appendIf(!isExtended, Component.text().append(Component.text("You do not have permission for any sub-commands!")).red().build())
                .sendTo(context.source)
        }
    }

    private inner class VersionCommand(
        private val helpUsage: String,
        private val extended: (TCommandSource) -> Boolean,
    ) : SimpleCommand<TCommandSource> {
        override fun execute(context: CommandContext<TCommandSource>) {
            val isExtended = extended(context.source)
            Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text().append(Component.text("Running version ")).aqua())
                .append(Component.text().append(Component.text(pluginInfo.version)).green())
                .append(Component.text().append(Component.text(" by ")).aqua())
                .appendJoining(", ", *pluginInfo.authors)
                .append(Component.text().append(Component.text("\nBuild date: ")).gray())
                .append(Component.text().append(Component.text("${pluginInfo.buildDate}\n")).aqua())
                .appendIf(isExtended, Component.text()
                    .append(Component.text().append(Component.text("Use ")).green())
                    .append(Component.text().append(Component.text(helpUsage)).gold())
                    .append(Component.text().append(Component.text(" for help")).green())
                    .build()
                )
                .appendIf(!isExtended, Component.text().append(Component.text("You do not have permission for any sub-commands")).red().build())
                .sendTo(context.source)
        }

        override fun shortDescription(source: TCommandSource): Component = VERSION_DESCRIPTION
    }

    private inner class HelpCommand(
        private val subCommandsSupplier: Supplier<List<CommandMapping<SimpleCommand<TCommandSource>>>>,
    ) : SimpleCommand<TCommandSource> {
        override fun execute(context: CommandContext<TCommandSource>) {
            val helpList = subCommandsSupplier.get().asSequence()
                .filter { it.command.canExecute(context.source) }
                .map { mapping ->
                    val command = mapping.command
                    val fullPath = mapping.getFullPath()
                    val otherAliases = mapping.otherAliases
                    val builder = Component.text().append(Component.text().append(fullPath).gold().build())
                    command.shortDescription(context.source)?.let { builder.append(Component.text("- ").append(it)) }
                    command.longDescription(context.source)?.let { builder.append(Component.text("\n").append(it)) }
                    builder.append(Component.text().append(Component.text("\nUsage: $fullPath")).gray().build())
                    command.usage(context.source)?.let { usage ->
                        builder.appendIf(otherAliases.isNotEmpty(), Component.text(", "))
                            .appendJoining(", ", *otherAliases.toTypedArray())
                            //Only append a space before the usage if the aliases are empty
                            .appendIf(otherAliases.isNotEmpty(), Component.text(" "))
                            .append(usage)
                    }
                    builder.build()
                }.toList()
            val pagination = Pagination.builder()
                .resultsPerPage(helpList.size)
                .build(
                    Component.text().append(Component.text(pluginInfo.name + " - " + pluginInfo.organizationName)).gold().build(),
                    Renderer()
                ) { page -> "/page $page" }

            val rendered = pagination.render(helpList, 1)
            for (i in helpList.indices) {
                rendered[i].sendTo(context.source)
            }
        }

        private inner class Renderer: Pagination.Renderer.RowRenderer<Component> {
            val rows = mutableListOf<Component>()
            override fun renderRow(value: Component?, index: Int): MutableCollection<Component> {
                if (rows.contains(value)) {
                    return mutableSetOf()
                }
                rows.add(index, value ?: Component.text(""))
                return mutableSetOf(value ?: Component.text(""))
            }

        }

        override fun shortDescription(source: TCommandSource): Component = HELP_DESCRIPTION
    }

    private inner class ReloadCommand : SimpleCommand<TCommandSource> {
        override fun execute(context: CommandContext<TCommandSource>) {
            environment.reload()
            successfullyReloaded.sendTo(context.source)
        }
    }

    override fun mapRouting(
        aliases: List<String>,
        rootCommand: SimpleCommand<TCommandSource>?,
        subCommands: List<CommandMapping<SimpleCommand<TCommandSource>>>,
        childCommandFallback: Boolean,
    ): CommandMapping<SimpleCommand<TCommandSource>> {
        return RoutingMapping(aliases, rootCommand, subCommands, childCommandFallback)
    }

    override fun mapTerminal(
        aliases: List<String>,
        command: SimpleCommand<TCommandSource>,
    ): CommandMapping<SimpleCommand<TCommandSource>> {
        return BaseCommandMapping(aliases, command)
    }

    override fun generateRoot(
        helpUsage: String,
        extended: Predicate<TCommandSource>,
    ): SimpleCommand<TCommandSource> {
        return RootCommand(helpUsage, extended::test)
    }

    override fun generateVersion(
        helpUsage: String,
        extended: Predicate<TCommandSource>,
    ): SimpleCommand<TCommandSource> {
        return VersionCommand(helpUsage, extended::test)
    }

    override fun generateHelp(
        subCommandsSupplier: Supplier<List<CommandMapping<SimpleCommand<TCommandSource>>>>,
    ): SimpleCommand<TCommandSource> {
        return HelpCommand(subCommandsSupplier)
    }

    override fun generateReload(): SimpleCommand<TCommandSource> {
        return ReloadCommand()
    }
}
