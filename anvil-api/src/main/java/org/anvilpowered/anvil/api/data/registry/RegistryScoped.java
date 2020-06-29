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

package org.anvilpowered.anvil.api.data.registry;

import org.anvilpowered.anvil.api.data.key.Key;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Used to indicate the persistence of a value or validity of a method in relation to reloads.
 * <p>
 * A field annotated with {@code RegistryScoped} must be persisted (cached) only between reloads
 * of that scope. For example, you may choose to persist a result calculated from a configurable
 * value (see {@link Registry}). This is illustrated here:
 * </p>
 * <pre><code>
 * public class FooService {
 *
 *     // this instance is persisted only for the duration of the scope
 *    {@literal @}RegistryScoped
 *     private Foo foo;
 *
 *     // used to calculate foo
 *     private final Registry registry;
 *
 *    {@literal @}Inject
 *     public FooService(Registry registry) {
 *         this.registry = registry;
 *         registry.whenLoaded(this::loaded).register();
 *     }
 *
 *     public void loaded() {
 *         // invalidate foo
 *         foo = null;
 *     }
 *
 *     // instances returned here are persisted only for the duration of the scope
 *    {@literal @}RegistryScoped
 *     public Foo getFoo() {
 *         if (foo == null) {
 *             foo = ...; // use registry for lazy initialization of foo
 *         }
 *         return foo;
 *     }
 * }
 * </code></pre>
 * <p>
 * A method annotated with {@code RegistryScoped} must be invoked inside of a reloaded listener
 * of the same scope. An example of this is {@link Registry#set(Key, Object)}. Although it has a
 * void return type, the side effects of this method will only persist during a scope and so must
 * be invoked again to maintain these side effects.
 * </p>
 */
@Target({
    FIELD,
    METHOD,
})
public @interface RegistryScoped {

    RegistryScope value() default RegistryScope.DEFAULT;
}
