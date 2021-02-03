/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api.event;
/*
 * Order that {@link Listener}d methods may be registered at.
 *
 * <p>Event handlers are called the order given in the following table.</p>
 *
 * <table>
 *     <tr><td>Order</td>       <td>Read Only</td> <td>Cancellation Allowed</td>
 *         <td>Recommendation</td>
 *     </tr>
 *     <tr><td>PRE</td>         <td>YES</td>       <td>NO</td>
 *         <td>Initialisation and registration actions</td>
 *     </tr>
 *     <tr><td>AFTER_PRE</td>   <td>YES</td>       <td>NO</td>
 *         <td>Immediate responses to actions in PRE</td>
 *     </tr>
 *     <tr><td>FIRST</td>       <td>YES</td>       <td>YES</td>
 *         <td>Cancellation by protection plugins for informational purposes</td>
 *     </tr>
 *     <tr><td>EARLY</td>       <td>NO</td>        <td>YES</td>
 *         <td>Standard actions that should happen before other plugins react to
 *         the event</td>
 *     </tr>
 *     <tr><td>DEFAULT</td>     <td>NO</td>        <td>YES</td>
 *         <td>The default action order</td>
 *     </tr>
 *     <tr><td>LATE</td>        <td>NO</td>        <td>YES</td>
 *         <td>Standard actions that should happen after other plugins react to the event</td>
 *     </tr>
 *     <tr><td>LAST</td>        <td>YES</td>       <td>YES</td>
 *         <td>Final cancellation by protection plugins</td>
 *     </tr>
 *     <tr><td>BEFORE_POST</td> <td>YES</td>       <td>NO</td>
 *         <td>Actions that need to respond to cancelled events before POST</td>
 *     </tr>
 *     <tr><td>POST</td>        <td>YES</td>       <td>NO</td>
 *         <td>Actions that need to react to the final and stable effects of event</td>
 *     </tr>
 * </table>
 *
 * <p>
 * Original source https://github.com/SpongePowered/SpongeAPI/blob/stable-7/src/main/java/org/spongepowered/api/event/Order.java
 * </p>
 */
public enum Order {

    /**
     * The order point of PRE handles setting up things that need to be done
     * before other things are handled PRE is read only and cannot cancel the
     * events.
     */
    PRE,

    /**
     * The order point of AFTER_PRE handles things that need to be done after
     * PRE AFTER_PRE is read only and cannot cancel the events.
     */
    AFTER_PRE,

    /**
     * The order point of FIRST handles cancellation by protection plugins for
     * informational responses FIRST is read only but can cancel events.
     */
    FIRST,

    /**
     * The order point of EARLY handles standard actions that need to be done
     * before other plugins EARLY is not read only and can cancel events.
     */
    EARLY,

    /**
     * The order point of DEFAULT handles just standard event handlings, you
     * should use this unless you know you need otherwise DEFAULT is not read
     * only and can cancel events.
     */
    DEFAULT,

    /**
     * The order point of LATE handles standard actions that need to be done
     * after other plugins LATE is not read only and can cancel the event.
     */
    LATE,

    /**
     * The order point of LAST handles last minute cancellations by protection
     * plugins LAST is read only but can cancel events.
     */
    LAST,

    /**
     * The order point of BEFORE_POST handles preparation for things needing
     * to be done in post BEFORE_POST is read only and cannot cancel events.
     */
    BEFORE_POST,

    /**
     * The order point of POST handles last minute things and monitoring
     * of events for rollback or logging POST is read only and
     * cannot cancel events.
     */
    POST,
}
