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

package org.anvilpowered.anvil.api.event;

import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import java.util.concurrent.CompletableFuture;

public interface EventManager {

  void register(Object listener);

  void register(Class<?> listener);

  void register(Key<?> listener);

  void register(TypeLiteral<?> listener);

  void register(TypeToken<?> listener);

  /**
   * Posts the provided event to all of its listeners
   *
   * @param event {@link Event} to post
   * @param maxWait How long to wait to collect results at end (only relevant if {@link Event#isExternallyBlockable()} is false)
   * @return {@code true} if the listeners completed successfully
   * and the event was not cancelled, otherwise {@code false}
   */
  <E extends Event> CompletableFuture<EventPostResult<E>> post(Class<E> eventType, E event, long maxWait);

  /**
   * Posts the provided event to all of its listeners
   *
   * @param event {@link Event} to post
   * @return {@code true} if the listeners completed successfully
   * and the event was not cancelled, otherwise {@code false}
   */
  <E extends Event> CompletableFuture<EventPostResult<E>> post(Class<E> eventType, E event);

  <E extends Event> void register(EventListener<E> listener);
}
