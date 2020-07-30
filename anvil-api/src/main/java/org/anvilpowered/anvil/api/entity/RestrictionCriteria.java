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

package org.anvilpowered.anvil.api.entity;

import com.google.common.base.MoreObjects;

public final class RestrictionCriteria {

    private static final RestrictionCriteria ALL = new RestrictionCriteria(
        true,
        true,
        true,
        true,
        true
    );

    private static final RestrictionCriteria NONE = new RestrictionCriteria(
        false,
        false,
        false,
        false,
        false
    );

    private static final RestrictionCriteria MOVEMENT_ONLY = new RestrictionCriteria(
        true,
        false,
        false,
        false,
        false
    );

    private static final RestrictionCriteria INTERACTION_ONLY = new RestrictionCriteria(
        false,
        true,
        false,
        false,
        false
    );

    private static final RestrictionCriteria INVENTORY_ONLY = new RestrictionCriteria(
        false,
        false,
        true,
        false,
        false
    );

    private static final RestrictionCriteria COMMANDS_ONLY = new RestrictionCriteria(
        false,
        false,
        false,
        true,
        false
    );

    private static final RestrictionCriteria DAMAGE_ONLY = new RestrictionCriteria(
        false,
        false,
        false,
        false,
        true
    );

    public static RestrictionCriteria all() {
        return ALL;
    }

    public static RestrictionCriteria none() {
        return NONE;
    }

    public static RestrictionCriteria movementOnly() {
        return MOVEMENT_ONLY;
    }

    public static RestrictionCriteria interactionOnly() {
        return INTERACTION_ONLY;
    }

    public static RestrictionCriteria inventoryOnly() {
        return INVENTORY_ONLY;
    }

    public static RestrictionCriteria commandsOnly() {
        return COMMANDS_ONLY;
    }

    public static RestrictionCriteria damageOnly() {
        return DAMAGE_ONLY;
    }

    public final static class Builder {
        private boolean movement = false;
        private boolean interaction = false;
        private boolean inventory = false;
        private boolean commands = false;
        private boolean damage = false;

        private Builder() {
        }

        public Builder movement(boolean movement) {
            this.movement = movement;
            return this;
        }

        public Builder interaction(boolean interaction) {
            this.interaction = interaction;
            return this;
        }

        public Builder inventory(boolean inventory) {
            this.inventory = inventory;
            return this;
        }

        public Builder commands(boolean commands) {
            this.commands = commands;
            return this;
        }

        public Builder damage(boolean damage) {
            this.damage = damage;
            return this;
        }

        public RestrictionCriteria build() {
            return new RestrictionCriteria(
                movement,
                interaction,
                inventory,
                commands,
                damage
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Prevents entity movement. This includes all movement keys (WASD + space) in addition to external influences.
     */
    private final boolean movement;

    /**
     * Prevents all interactions that occurs as the result of a right-click.
     */
    private final boolean interaction;

    /**
     * Prevents all inventory transactions.
     */
    private final boolean inventory;

    /**
     * Prevents all commands.
     */
    private final boolean commands;

    /**
     * Prevents damage dealt and taken.
     */
    private final boolean damage;

    public RestrictionCriteria(
        boolean movement,
        boolean interaction,
        boolean inventory,
        boolean commands,
        boolean damage
    ) {
        this.movement = movement;
        this.interaction = interaction;
        this.inventory = inventory;
        this.commands = commands;
        this.damage = damage;
    }

    public RestrictionCriteria union(RestrictionCriteria criteria) {
        final boolean movement = this.movement || criteria.movement;
        final boolean interaction = this.interaction || criteria.interaction;
        final boolean inventory = this.inventory || criteria.inventory;
        final boolean commands = this.commands || criteria.commands;
        final boolean damage = this.damage || criteria.damage;
        return new RestrictionCriteria(
            movement,
            interaction,
            inventory,
            commands,
            damage
        );
    }

    public RestrictionCriteria intersect(RestrictionCriteria criteria) {
        final boolean movement = this.movement && criteria.movement;
        final boolean interaction = this.interaction && criteria.interaction;
        final boolean inventory = this.inventory && criteria.inventory;
        final boolean commands = this.commands && criteria.commands;
        final boolean damage = this.damage && criteria.damage;
        return new RestrictionCriteria(
            movement,
            interaction,
            inventory,
            commands,
            damage
        );
    }

    public boolean hasAll() {
        return movement
            && interaction
            && inventory
            && commands
            && damage
            ;
    }

    public boolean hasAny() {
        return movement
            || interaction
            || inventory
            || commands
            || damage
            ;
    }

    public boolean movement() {
        return movement;
    }

    public boolean interaction() {
        return interaction;
    }

    public boolean inventory() {
        return inventory;
    }

    public boolean commands() {
        return commands;
    }

    public boolean damage() {
        return damage;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RestrictionCriteria)) {
            return false;
        }
        final RestrictionCriteria other = (RestrictionCriteria) obj;
        return movement == other.movement
            && interaction == other.interaction
            && inventory == other.inventory
            && commands == other.commands
            && damage == other.damage
            ;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("movement", movement)
            .add("interaction", interaction)
            .add("inventory", inventory)
            .add("commands", commands)
            .add("damage", damage)
            .toString();
    }
}
