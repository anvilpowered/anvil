/*
 *   Anvil - Registry
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
package org.anvilpowered.anvil.api.registry.scope

import org.anvilpowered.anvil.api.registry.scope.RegistryReloadScope

/**
 * Used to indicate the persistence of a value or validity of a method in relation to reloads.
 *
 *
 * A field annotated with `RegistryScoped` must be persisted (cached) only between reloads
 * of that scope. For example, you may choose to persist a result calculated from a configurable
 * value (see [Registry]). This is illustrated here:
 *
 * <pre>`
 * public class FooService {
 *
 * // this instance is persisted only for the duration of the scope
 * @RegistryScoped
 * private Foo foo;
 *
 * // used to calculate foo
 * private final Registry registry;
 *
 * @Inject
 * public FooService(Registry registry) {
 * this.registry = registry;
 * registry.whenLoaded(this::loaded).register();
 * }
 *
 * public void loaded() {
 * // invalidate foo
 * foo = null;
 * }
 *
 * // instances returned here are persisted only for the duration of the scope
 * @RegistryScoped
 * public Foo getFoo() {
 * if (foo == null) {
 * foo = ...; // use registry for lazy initialization of foo
 * }
 * return foo;
 * }
 * }
`</pre> *
 *
 *
 * A method annotated with `RegistryScoped` must be invoked inside of a reloaded listener
 * of the same scope. An example of this is [Registry.set]. Although it has a
 * void return type, the side effects of this method will only persist during a scope and so must
 * be invoked again to maintain these side effects.
 *
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY)
annotation class RegistryScoped(val value: RegistryReloadScope = RegistryReloadScope.DEFAULT)
