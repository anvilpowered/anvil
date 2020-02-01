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

import java.net.URL;
import java.util.function.Consumer;

public interface StringResult<TString, TCommandSource> extends Result<TString, String> {
    Builder<TString, TCommandSource> builder();
    void send(TString result, TCommandSource commandSource);
    TString deserialize(String text);
    String serialize(TString text);
    String removeColor(String text);
    TString withoutColor(String text);
    interface Builder<TString, TCommandSource> {
        /* colors apply to text AFTER called (until new color is specified) */
        Builder<TString, TCommandSource> aqua();
        Builder<TString, TCommandSource> black();
        Builder<TString, TCommandSource> blue();
        Builder<TString, TCommandSource> dark_aqua();
        Builder<TString, TCommandSource> dark_blue();
        Builder<TString, TCommandSource> dark_gray();
        Builder<TString, TCommandSource> dark_green();
        Builder<TString, TCommandSource> dark_purple();
        Builder<TString, TCommandSource> dark_red();
        Builder<TString, TCommandSource> gold();
        Builder<TString, TCommandSource> gray();
        Builder<TString, TCommandSource> green();
        Builder<TString, TCommandSource> light_purple();
        Builder<TString, TCommandSource> red();
        Builder<TString, TCommandSource> reset();
        Builder<TString, TCommandSource> white();
        Builder<TString, TCommandSource> yellow();

        Builder<TString, TCommandSource> append(Object... content);
        Builder<TString, TCommandSource> appendJoining(Object delimiter, Object... content);
        Builder<TString, TCommandSource> onHoverShowText(TString content);
        Builder<TString, TCommandSource> onHoverShowText(Builder<TString, TCommandSource> builder);
        Builder<TString, TCommandSource> onClickSuggestCommand(String command);
        Builder<TString, TCommandSource> onClickRunCommand(String command);
        Builder<TString, TCommandSource> onClickExecuteCallback(Consumer<TCommandSource> callback);
        Builder<TString, TCommandSource> onClickOpenUrl(URL url);
        Builder<TString, TCommandSource> onClickOpenUrl(String url);
        TString build();
        void sendTo(TCommandSource commandSource);
    }
}
