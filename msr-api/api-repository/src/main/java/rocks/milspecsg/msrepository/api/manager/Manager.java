/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msrepository.api.manager;

import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;


public interface Manager<T extends ObjectWithId<?>, R extends Repository<?, T, ?>> {

    /**
     * Represents the default singular identifier for a {@link ObjectWithId}
     * <p>
     * Should be overridden by other plugins who change the name of the object.
     * Examples: "Clan", "Faction", "Guild", "Member", ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierSingularUpper();

    /**
     * Represents the default plural identifier for a {@link ObjectWithId}
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "Clans", "Factions", "Guilds", "Members" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierPluralUpper();

    /**
     * Represents the default singular identifier for a {@link ObjectWithId}
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clan", "faction", "guild", "member" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierSingularLower();

    /**
     * Represents the default plural identifier for a {@link ObjectWithId}
     * <p>
     * Should be overridden by other plugins who change the name of party.
     * Examples: "clans", "factions", "guilds", "members" ... etc
     * </p>
     * <p>
     * Used in text sent to the player
     * </p>
     */
    String getDefaultIdentifierPluralLower();

    R getPrimaryRepository();

}
