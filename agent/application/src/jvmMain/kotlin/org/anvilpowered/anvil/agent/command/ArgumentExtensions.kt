package org.anvilpowered.anvil.agent.command

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import org.anvilpowered.anvil.user.CommandSource
import org.anvilpowered.anvil.user.hasPermissionSet

internal fun <T : ArgumentBuilder<CommandSource, T>> T.requiresPermission(permission: String): T =
    requires { it.hasPermissionSet(permission) }

internal fun <T : ArgumentBuilder<CommandSource, T>> T.executesSingleSuccess(block: (CommandContext<CommandSource>) -> Unit) =
    executes { context ->
        block(context)
        1
    }
