/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.common.util;

import rocks.milspecsg.anvil.api.util.StringResult;

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

    protected static abstract class CommonStringResultBuilder<TString, TCommandSource> implements Builder<TString, TCommandSource> {

        @Override
        public Builder<TString, TCommandSource> append(CharSequence... content) {
            return append((Object[]) content);
        }

        @Override
        public Builder<TString, TCommandSource> appendJoining(Object delimiter, CharSequence... content) {
            return append(delimiter, (Object[]) content);
        }
    }
}
