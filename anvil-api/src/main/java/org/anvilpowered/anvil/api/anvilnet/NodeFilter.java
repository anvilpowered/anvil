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

package org.anvilpowered.anvil.api.anvilnet;

import org.anvilpowered.anvil.api.Anvil;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface NodeFilter extends Predicate<AnvilNetNode> {

    static NodeFilter none() {
        return builder().all().exclude().build();
    }

    static NodeFilter all() {
        return builder().build();
    }

    static NodeFilter proxyOnly() {

    }

    static Builder builder() {
        return Anvil.getServiceManager().provide(Builder.class);
    }

    interface Builder {

        interface Matcher {

            Builder include();

            Builder exclude();
        }

        Matcher all();

        Matcher platformName(String platform);

        Matcher platformIsProxy(boolean isProxy);

        Matcher nodeName(String name);

        Matcher nodeName(Pattern name);

        NodeFilter build();
    }
}
