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

package org.anvilpowered.anvil.api.datastore;

import com.google.inject.name.Named;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.util.TextService;

/**
 * <p>
 * A module consists of a {@link Manager} and a (single) {@link Component}
 * for every data storage implementation.
 * </p>
 * <p>
 * The {@link Manager} of a module is its metaphorical gateway. Interactions with
 * other modules should (almost always) be done through the {@link Manager}.
 * There are, however, some cases where direct access to a component is required.
 * One such case is inter-{@link Repository} access that requires compile time
 * type safety. Because the {@link Component#getTKeyClass()} type is not known
 * to the manager, code that interacts with {@code TKey} must be placed in a
 * {@link Component}.
 * </p>
 * <p>
 * One of the primary functions of a {@link Manager} is to provide the correct
 * {@link Component} implementation via {@link #getPrimaryComponent()}.
 * </p>
 * <p>
 * Implementations of {@link Manager} should consist of methods similar to the
 * following:
 * </p>
 * <ul>
 *     <li>{@code CompletableFuture<TString> create(UUID userUUID);}</li>
 *     <li>{@code CompletableFuture<TString> invite(UUID userUUID, UUID targetUserUUID);}</li>
 *     <li>{@code CompletableFuture<TString> kick(UUID userUUID, UUID targetUserUUID);}</li>
 *     <li>{@code CompletableFuture<List<TString>> list(String query);}</li>
 * </ul>
 * <p>
 * {@code TString} is the base return type for the methods in a {@link Manager}.
 * To build these results use {@link TextService.Builder}.
 * </p>
 * <p>
 * All methods (with some exceptions) in {@link Manager} should return a form of {@code TString}
 * to be displayed directly to the end user. Normally, the return type, {@code TString}, is wrapped in
 * a {@link java.util.concurrent.CompletableFuture} in order to keep the main game thread
 * free from IO. It is sometimes necessary to further wrap the {@code TString} in a {@link java.util.List}
 * when the result is more than a single line. In this case, pagination can be used to display the result
 * to the end user.
 * </p>
 * <p>
 * The following interface signature is an example of a simple {@link Manager}:
 * </p>
 * <pre>{@code
 * public interface FooManager<
 * TFoo extends Foo<?>,
 * TString>
 * extends Manager<FooRepository<?, TFoo, ?, ?>>
 * }</pre>
 *
 * @param <C> Base {@link Component} type for this manager.
 *            Must be implemented by all components in this module
 * @see Repository
 * @see Component
 * @see TextService
 */
public interface Manager<C extends Component<?, ?>> {

    /**
     * <p>
     * Represents the default singular identifier for this module
     * </p>
     * <p>
     * Should be overridden by other plugins who change the name of the object.
     * Examples: "Clan", "Faction", "Guild", "Member", ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return The default singular identifier for this module
     */
    String getDefaultIdentifierSingularUpper();

    /**
     * <p>
     * Represents the default plural identifier for this module
     * </p>
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "Clans", "Factions", "Guilds", "Members" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return The default plural identifier for this module
     */
    String getDefaultIdentifierPluralUpper();

    /**
     * <p>
     * Represents the default singular identifier for this module
     * </p>
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clan", "faction", "guild", "member" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return The default singular identifier for this module
     */
    String getDefaultIdentifierSingularLower();

    /**
     * <p>
     * Represents the default plural identifier for this module
     * </p>
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clans", "factions", "guilds", "members" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     *
     * @return The default plural identifier for this module
     */
    String getDefaultIdentifierPluralLower();

    /**
     * Provides the current {@link Component} as defined by
     * {@link Keys#DATA_STORE_NAME} in the current {@link Registry}.
     *
     * <p>
     * The current {@link Component} implementation is defined as the
     * implementation provided by Guice that meets the following criteria:
     * </p>
     * <br>
     * <p>
     * The value for {@link Keys#DATA_STORE_NAME} found by
     * the the current {@link Registry} must match (ignored case) a registered
     * datastore implementation. This can be one of the following predefined values:
     * </p>
     * <ul>
     *   <li>{@code "mongodb"}</li>
     *   <li>{@code "xodus"}</li>
     * </ul>
     * <p>
     * or a custom value defined by your guice module.
     * </p>
     * <br>
     * <p>
     * For example, 'mongodb' (or any capitalization thereof) will match
     * a {@link Component} annotated with {@link Named}{@code (value = "mongodb"}
     * </p>
     * <p>
     * Use {@link BindingExtensions} to bind your component implementations
     * </p>
     *
     * @return The current {@link Component} implementation
     * @throws IllegalStateException If the config has not been loaded yet,
     *                               or if no implementation was found
     * @see Component
     * @see BindingExtensions
     */
    C getPrimaryComponent();
}
