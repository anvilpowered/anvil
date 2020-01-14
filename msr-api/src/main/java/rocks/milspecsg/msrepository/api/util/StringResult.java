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

package rocks.milspecsg.msrepository.api.util;

import java.util.function.Consumer;

public interface StringResult<TResult, TCommandSource> extends Result<TResult, String> {
    StringResult.Builder<TResult, TCommandSource> builder();
    void send(TResult result, TCommandSource commandSource);
    TResult deserialize(String text);
    String serialize(TResult text);
    interface Builder<TResult, TCommandSource> {
        /* colors apply to text AFTER called (until new color is specified) */
        Builder<TResult, TCommandSource> aqua();
        Builder<TResult, TCommandSource> black();
        Builder<TResult, TCommandSource> blue();
        Builder<TResult, TCommandSource> dark_aqua();
        Builder<TResult, TCommandSource> dark_blue();
        Builder<TResult, TCommandSource> dark_gray();
        Builder<TResult, TCommandSource> dark_green();
        Builder<TResult, TCommandSource> dark_purple();
        Builder<TResult, TCommandSource> dark_red();
        Builder<TResult, TCommandSource> gold();
        Builder<TResult, TCommandSource> gray();
        Builder<TResult, TCommandSource> green();
        Builder<TResult, TCommandSource> light_purple();
        Builder<TResult, TCommandSource> red();
        Builder<TResult, TCommandSource> reset();
        Builder<TResult, TCommandSource> white();
        Builder<TResult, TCommandSource> yellow();

        Builder<TResult, TCommandSource> append(Object... content);
        Builder<TResult, TCommandSource> appendJoining(Object delimiter, Object... content);
        Builder<TResult, TCommandSource> onHoverShowText(TResult content);
        Builder<TResult, TCommandSource> onHoverShowText(Builder<TResult, TCommandSource> builder);
        Builder<TResult, TCommandSource> onClickSuggestCommand(String command);
        Builder<TResult, TCommandSource> onClickRunCommand(String command);
        Builder<TResult, TCommandSource> onClickExecuteCallback(Consumer<TCommandSource> callback);
        TResult build();
        void sendTo(TCommandSource commandSource);
    }
}
