/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.core.db

import kotlin.reflect.KProperty1

interface Pagination<F : DomainFacet<*>> {
    fun limit(n: Int, offset: Long = 0): Pagination<F>
    fun sortBy(field: KProperty1<F, Comparable<*>>, direction: SortDirection = SortDirection.ASCENDING): Pagination<F>
    fun build(): List<F>
}

enum class SortDirection {
    ASCENDING,
    DESCENDING,
}
