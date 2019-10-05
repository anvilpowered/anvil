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
