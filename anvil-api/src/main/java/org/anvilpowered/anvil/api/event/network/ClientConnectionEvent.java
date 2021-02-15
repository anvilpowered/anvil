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

package org.anvilpowered.anvil.api.event.network;

import org.anvilpowered.anvil.api.event.Cancellable;
import org.anvilpowered.anvil.api.event.Event;
import org.anvilpowered.anvil.api.event.message.MessageEvent;
import org.anvilpowered.anvil.api.event.user.TargetUserEvent;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;

public interface ClientConnectionEvent extends Event {

    String getConnection();



    interface Auth extends ClientConnectionEvent, Cancellable {

        interface InnerAuth extends Auth {

        }
    }

    interface Login<TString>
        extends ClientConnectionEvent, MessageEvent<TString>, TargetUserEvent, Cancellable {

        CoreMember<?> getCoreMember();
    }

    interface Disconnect extends ClientConnectionEvent {

    }
}
