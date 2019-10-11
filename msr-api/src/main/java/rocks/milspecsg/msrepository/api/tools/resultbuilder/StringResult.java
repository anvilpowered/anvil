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

package rocks.milspecsg.msrepository.api.tools.resultbuilder;

public interface StringResult<TResult> extends Result<TResult, String> {
    StringResult.Builder<TResult> builder();
    interface Builder<TResult> {
        Builder<TResult> aqua();
        Builder<TResult> black();
        Builder<TResult> blue();
        Builder<TResult> dark_aqua();
        Builder<TResult> dark_blue();
        Builder<TResult> dark_gray();
        Builder<TResult> dark_green();
        Builder<TResult> dark_purple();
        Builder<TResult> dark_red();
        Builder<TResult> gold();
        Builder<TResult> gray();
        Builder<TResult> green();
        Builder<TResult> light_purple();
        Builder<TResult> red();
        Builder<TResult> reset();
        Builder<TResult> white();
        Builder<TResult> yellow();
        Builder<TResult> append(Object... content);
        TResult build();
    }
}
