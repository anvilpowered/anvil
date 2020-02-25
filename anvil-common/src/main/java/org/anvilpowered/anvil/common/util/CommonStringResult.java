/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.common.util;

import org.anvilpowered.anvil.api.util.StringResult;

public abstract class CommonStringResult<TString, TCommandSource> implements StringResult<TString, TCommandSource> {

    @Override
    public TString success(String s) {
        return builder().green().append(s).build();
    }

    @Override
    public TString fail(String s) {
        return builder().red().append(s).build();
    }

    @Override
    public String removeColor(String text) {
        return text.replaceAll("&[0-9a-fklmnor]", "");
    }

    @Override
    public TString withoutColor(String text) {
        return builder().append(removeColor(text)).build();
    }

    protected abstract class CommonStringResultBuilder implements Builder<TString, TCommandSource> {

        @Override
        public Builder<TString, TCommandSource> append(CharSequence... content) {
            return append((Object[]) content);
        }

        @Override
        public Builder<TString, TCommandSource> appendIf(boolean condition, Object... content) {
            return condition ? append(content) : this;
        }

        @Override
        public Builder<TString, TCommandSource> appendIf(boolean condition, CharSequence... content) {
            return condition ? append(content) : this;
        }

        @Override
        public Builder<TString, TCommandSource> appendJoining(Object delimiter, CharSequence... content) {
            return appendJoining(delimiter, (Object[]) content);
        }

        @Override
        public Builder<TString, TCommandSource> appendJoiningIf(boolean condition, Object delimiter, Object... content) {
            return condition ? appendJoining(delimiter, content) : this;
        }

        @Override
        public Builder<TString, TCommandSource> appendJoiningIf(boolean condition, Object delimiter, CharSequence... content) {
            return condition ? appendJoining(delimiter, content) : this;
        }

        @Override
        public void sendTo(TCommandSource commandSource) {
            send(build(), commandSource);
        }

        @Override
        public void sendToConsole() {
            CommonStringResult.this.sendToConsole(build());
        }
    }
}
