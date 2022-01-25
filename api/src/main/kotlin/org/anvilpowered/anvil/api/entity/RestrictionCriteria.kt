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
package org.anvilpowered.anvil.api.entity

import com.google.common.base.MoreObjects

class RestrictionCriteria(
    /**
     * Prevents entity movement. This includes all movement keys (WASD + space) in addition to external influences.
     */
    private val movement: Boolean,
    /**
     * Prevents all interactions that occurs as the result of a right-click.
     */
    private val interaction: Boolean,
    /**
     * Prevents all inventory transactions.
     */
    private val inventory: Boolean,
    /**
     * Prevents all commands.
     */
    private val commands: Boolean,
    /**
     * Prevents damage dealt and taken.
     */
    private val damage: Boolean
) {
    class Builder {
        private var movement = false
        private var interaction = false
        private var inventory = false
        private var commands = false
        private var damage = false
        fun movement(movement: Boolean): Builder {
            this.movement = movement
            return this
        }

        fun interaction(interaction: Boolean): Builder {
            this.interaction = interaction
            return this
        }

        fun inventory(inventory: Boolean): Builder {
            this.inventory = inventory
            return this
        }

        fun commands(commands: Boolean): Builder {
            this.commands = commands
            return this
        }

        fun damage(damage: Boolean): Builder {
            this.damage = damage
            return this
        }

        fun build(): RestrictionCriteria {
            return RestrictionCriteria(
                movement,
                interaction,
                inventory,
                commands,
                damage
            )
        }
    }

    fun union(criteria: RestrictionCriteria): RestrictionCriteria {
        val movement = movement || criteria.movement
        val interaction = interaction || criteria.interaction
        val inventory = inventory || criteria.inventory
        val commands = commands || criteria.commands
        val damage = damage || criteria.damage
        return RestrictionCriteria(
            movement,
            interaction,
            inventory,
            commands,
            damage
        )
    }

    fun intersect(criteria: RestrictionCriteria): RestrictionCriteria {
        val movement = movement && criteria.movement
        val interaction = interaction && criteria.interaction
        val inventory = inventory && criteria.inventory
        val commands = commands && criteria.commands
        val damage = damage && criteria.damage
        return RestrictionCriteria(
            movement,
            interaction,
            inventory,
            commands,
            damage
        )
    }

    fun hasAll(): Boolean {
        return (movement
            && interaction
            && inventory
            && commands
            && damage)
    }

    fun hasAny(): Boolean {
        return (movement
            || interaction
            || inventory
            || commands
            || damage)
    }

    fun movement(): Boolean {
        return movement
    }

    fun interaction(): Boolean {
        return interaction
    }

    fun inventory(): Boolean {
        return inventory
    }

    fun commands(): Boolean {
        return commands
    }

    fun damage(): Boolean {
        return damage
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is RestrictionCriteria) {
            return false
        }
        return movement == obj.movement && interaction == obj.interaction && inventory == obj.inventory && commands == obj.commands && damage == obj.damage
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("movement", movement)
            .add("interaction", interaction)
            .add("inventory", inventory)
            .add("commands", commands)
            .add("damage", damage)
            .toString()
    }

    companion object {
        private val ALL = RestrictionCriteria(
            true,
            true,
            true,
            true,
            true
        )
        private val NONE = RestrictionCriteria(
            false,
            false,
            false,
            false,
            false
        )
        private val MOVEMENT_ONLY = RestrictionCriteria(
            true,
            false,
            false,
            false,
            false
        )
        private val INTERACTION_ONLY = RestrictionCriteria(
            false,
            true,
            false,
            false,
            false
        )
        private val INVENTORY_ONLY = RestrictionCriteria(
            false,
            false,
            true,
            false,
            false
        )
        private val COMMANDS_ONLY = RestrictionCriteria(
            false,
            false,
            false,
            true,
            false
        )
        private val DAMAGE_ONLY = RestrictionCriteria(
            false,
            false,
            false,
            false,
            true
        )

        fun all(): RestrictionCriteria {
            return ALL
        }

        fun none(): RestrictionCriteria {
            return NONE
        }

        fun movementOnly(): RestrictionCriteria {
            return MOVEMENT_ONLY
        }

        fun interactionOnly(): RestrictionCriteria {
            return INTERACTION_ONLY
        }

        fun inventoryOnly(): RestrictionCriteria {
            return INVENTORY_ONLY
        }

        fun commandsOnly(): RestrictionCriteria {
            return COMMANDS_ONLY
        }

        fun damageOnly(): RestrictionCriteria {
            return DAMAGE_ONLY
        }

        fun builder(): Builder {
            return Builder()
        }
    }
}
