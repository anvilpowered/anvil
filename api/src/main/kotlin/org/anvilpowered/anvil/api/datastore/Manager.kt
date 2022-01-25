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
package org.anvilpowered.anvil.api.datastore

import java.util.concurrent.CompletableFuture

/**
 *
 *
 * A module consists of a [Manager] and a (single) [DBComponent]
 * for every data storage implementation.
 *
 *
 *
 * The [Manager] of a module is its metaphorical gateway. Interactions with
 * other modules should (almost always) be done through the [Manager].
 * There are, however, some cases where direct access to a component is required.
 * One such case is inter-[Repository] access that requires compile time
 * type safety. Because the [DBComponent.tKeyClass] type is not known
 * to the manager, code that interacts with `TKey` must be placed in a
 * [DBComponent].
 *
 *
 *
 * One of the primary functions of a [Manager] is to provide the correct
 * [DBComponent] implementation via [.getPrimaryComponent].
 *
 *
 *
 * Implementations of [Manager] should consist of methods similar to the
 * following:
 *
 *
 *  * `[CompletableFuture]<[Component]> create(UUID userUUID);`
 *  * `[CompletableFuture]<[Component]> invite(UUID userUUID, UUID targetUserUUID);`
 *  * `[CompletableFuture]<[Component]> kick(UUID userUUID, UUID targetUserUUID);`
 *  * `[CompletableFuture]<List<[Component]>> list(String query);`
 *
 *
 *
 * All methods (with some exceptions) in [Manager] should return a form of [Component]
 * to be displayed directly to the end user. Normally, the return type, [Component], is wrapped in
 * a [java.util.concurrent.CompletableFuture] in order to keep the main game thread
 * free from IO. It is sometimes necessary to further wrap the [Component] in a [java.util.List]
 * when the result is more than a single line. In this case, pagination can be used to display the result
 * to the end user.
 *
 *
 *
 * The following is an example of a typical [Manager] and [Repository] combination:
 *
 * <pre>`
 * public interface FooManager
 * extends Manager<FooRepository<?, ?>> {...}
 *
 * public interface FooRepository<TKey, TDataStore>
 * extends Repository<TKey, Foo<TKey>, TDataStore> {...}
`</pre> *
 *
 * @param <C> Base [DBComponent] type for this manager.
 * Must be implemented by all components in this module
 * @see Repository
 *
 * @see DBComponent
</C> */
interface Manager<C : DBComponent<*, *>?> {
    /**
     * Provides the current [DBComponent] as defined by [Keys.DATA_STORE_NAME] in the current [Registry].
     *
     *
     * The current [DBComponent] implementation is defined as the implementation provided by Guice that meets the following
     * criteria:
     *
     * <br></br>
     *
     *
     * The value for [Keys.DATA_STORE_NAME] found by the current [Registry] must match (ignored case) a registered
     * datastore implementation. This can be one of the following predefined values:
     *
     *
     *  * `"mongodb"`
     *  * `"xodus"`
     *
     *
     *
     * or a custom value defined by your guice module.
     *
     * <br></br>
     *
     *
     * For example, 'mongodb' (or any capitalization thereof) will match
     * a [DBComponent] annotated with [Named]`(value = "mongodb")`
     *
     *
     * @return The current [DBComponent] implementation
     * @throws IllegalStateException If the config has not been loaded yet, or if no implementation was found
     * @see DBComponent
     */
    val primaryComponent: C
}
