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

import org.anvilpowered.anvil.api.anvilnet.AnvilNetNode;
import org.anvilpowered.anvil.api.anvilnet.AnvilNetService;
import org.anvilpowered.anvil.api.anvilnet.NodeFilter;

import java.util.function.Predicate;

public interface Event {

  /**
   * Determines whether or not this event runs off the "main thread". This is only applicable
   * to backend servers such as nukkit, spigot and sponge. Always returns true if the event
   * originated on a proxy server such as bungee and velocity.
   *
   * <p>
   * The result of this method <strong>may not change</strong> over the lifetime of this instance
   * </p>
   *
   * @return Whether this event runs off the "main thread" or originated on a proxy server
   */
  boolean isAsync();

  /**
   * Determines whether or not this event can be blocked (e.g. edited, cancelled) by listeners
   * outside of this JVM.
   *
   * <p>
   * Please note that it is <strong>strongly recommended</strong> to only enable this for async
   * events. An externally blockable "main thread" event would be absolutely catastrophic to
   * server performance.
   * </p>
   * <br>
   *
   * <p>
   * Listeners will be grouped by {@link Order}. All listeners for a given order execute in
   * parallel across all servers except for the source server which fires first. The execution
   * order is as follows: Firstly, the source server's {@link Order#PRE} listeners fire. After
   * they are finished, the {@link Order#PRE} listeners on the other servers fire. This is then
   * repeated for all following orders.
   * </p>
   * <br>
   *
   * <strong>If {@code true}</strong>
   * <ul>
   *   <li>
   *     Allows modifications from external listeners (on other servers i.e.
   *     {@link AnvilNetService}). Please note that this will add
   *     <strong>significant</strong> time to the execution of this
   *     event as it will be blocked until all listeners have finished.
   *   </li>
   *   <li>
   *     Listeners on all servers for any given order are blocked until all listeners for
   *     the previous order have finished. For example, a {@link Order#DEFAULT} order
   *     listener will not run until all {@link Order#EARLY} listeners on all servers are
   *     done. (Depending on implementation, there may be a timeout for the wait for each
   *     order)
   *   </li>
   * </ul>
   *
   * <strong>If {@code false}</strong>
   * <ul>
   *   <li>
   *     External listeners will only be able to <strong>read</strong> from the {@link Event} instance.
   *   </li>
   *   <li>
   *     Execution order and sequentiality is only guaranteed on the source server; Listeners
   *     on all non-source servers for any given order will not wait for the previous order
   *     to finish across all servers. It is entirely possible for the {@link Order#DEFAULT}
   *     listeners to start before the {@link Order#EARLY} listeners are finished (on a
   *     non-source server).
   *   </li>
   * </ul>
   *
   * @return Whether this event can be blocked by listeners outside of this JVM.
   */
  boolean isExternallyBlockable();

  /**
   * To be used as a guard condition for event listeners.
   *
   * <p>
   * The following code is an example of a listener that will only run for events coming from
   * the "minigames" server.
   * </p>
   * <pre><code>
   * {@literal @}Listener
   * public void myListener(SomeEvent event) {
   *   NodeFilter filter = NodeFilter.builder()
   *     // include only minigames server
   *     .name("minigames").include()
   *     .build();
   *
   *   // exit if its not from minigames
   *   if (event.notFrom(filter) {
   *     return;
   *   }
   * }
   * </code></pre>
   *
   * <p>
   * To listen to events coming from all servers <strong>except</strong> for the minigames
   * server, use {@code .name("minigames").exclude()} instead.
   * </p>
   *
   * @param filter The {@link Predicate} to test
   * @return Whether the provided filter does not match this {@code event}
   * @see NodeFilter
   */
  boolean notFrom(Predicate<AnvilNetNode> filter);

  /**
   * Merges an event of the same origin into this event. Used when the results from parallel
   * listeners need to be combined.
   *
   * @param event The event to merge into {@code this}
   * @param <E>   The Event type
   * @return {@code this}
   * @throws IllegalArgumentException If the provided event is not the same original event
   */
  <E extends Event> E merge(E event);
}
