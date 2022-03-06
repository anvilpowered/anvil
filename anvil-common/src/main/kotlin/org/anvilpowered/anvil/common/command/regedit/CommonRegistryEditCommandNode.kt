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

package org.anvilpowered.anvil.common.command.regedit

import com.google.inject.Inject
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.command.CommandNode
import org.anvilpowered.anvil.api.command.CommandService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode
import java.util.function.Function
import java.util.function.Predicate

abstract class CommonRegistryEditCommandNode<TCommandExecutor, TCommandSource>(
    val registry: Registry,
) : CommandNode<TCommandSource> {

    companion object {
        val CANCEL_ALIAS = listOf("cancel")
        val COMMIT_ALIAS = listOf("commit")
        val KEY_ALIAS = listOf("key")
        val SELECT_ALIAS = listOf("select")
        val START_ALIAS = listOf("start")
        val HELP_ALIAS = listOf("help")

        const val CANCEL_DESCRIPTION = "Cancel a stage"
        const val COMMIT_DESCRIPTION = "Commit changes"
        const val KEY_DESCRIPTION = "Do stuff with a key"
        const val SELECT_DESCRIPTION = "Select a registry"
        const val START_DESCRIPTION = "Start a regedit with the specified environment"
        const val ROOT_DESCRIPTION = "Root regedit command"

        const val KEY_USAGE = "<key> [info|set|unset|unstage] [<value>]"
        const val SELECT_USAGE = "<reg>"
        const val START_USAGE = "<env>"

        const val REGEDIT = "regedit"
        lateinit var PATH: Array<String>
    }

    private var alreadyLoaded = false
    private val descriptions: MutableMap<List<String>, Function<TCommandSource, String>> = HashMap()
    private val permissions: MutableMap<List<String>, Predicate<TCommandSource>> = HashMap()
    private val usages: MutableMap<List<String>, Function<TCommandSource, String>> = HashMap()

    @Inject
    protected lateinit var commandService: CommandService<TCommandExecutor, TCommandSource>

    @Inject
    protected lateinit var environment: Environment

    init {
        registry.whenLoaded {
            if (alreadyLoaded) return@whenLoaded
            PATH = arrayOf(CommonAnvilCommandNode.getAlias())
            loadCommands()
            alreadyLoaded = true
        }.order(-10).register() // has to load before main node
        descriptions.put(CANCEL_ALIAS) { CANCEL_DESCRIPTION }
        descriptions.put(COMMIT_ALIAS) { COMMIT_DESCRIPTION }
        descriptions.put(KEY_ALIAS) { KEY_DESCRIPTION }
        descriptions.put(SELECT_ALIAS) { SELECT_DESCRIPTION }
        descriptions.put(START_ALIAS) { START_DESCRIPTION }
        usages.put(KEY_ALIAS) { KEY_USAGE }
        usages.put(SELECT_ALIAS) { SELECT_USAGE }
        usages.put(START_ALIAS) { START_USAGE }
    }

    protected abstract fun loadCommands()
    override fun getName(): String = REGEDIT
    override fun getDescriptions(): Map<List<String>, Function<TCommandSource, String>> = descriptions
    override fun getPermissions(): Map<List<String>, Predicate<TCommandSource>> = permissions
    override fun getUsages(): Map<List<String>, Function<TCommandSource, String>> = usages
    override fun getPath(): Array<String> = PATH
}
