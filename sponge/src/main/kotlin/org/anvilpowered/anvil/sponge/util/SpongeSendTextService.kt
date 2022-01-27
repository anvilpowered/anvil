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

package org.anvilpowered.anvil.sponge.util

import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import org.anvilpowered.anvil.api.util.SendTextService
import org.spongepowered.api.Sponge
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.command.CommandCause

class SpongeSendTextService : SendTextService {

    override fun <T> send(recipient: T, component: Component) {
        if (recipient is CommandCause) {
            recipient.sendMessage(Identity.nil(), component)
        }
        if (recipient is SystemSubject) {
            recipient.sendMessage(component)
        }
    }

    override fun sendToConsole(component: Component) {
        Sponge.systemSubject().sendMessage(component)
    }
}
