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

package rocks.milspecsg.msrepository.velocity.util;

import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import rocks.milspecsg.msrepository.api.util.Result;


public class VelocityResult<TData> implements Result<TextComponent, TData> {

    @Override
    public TextComponent success(TData data) {
        return TextComponent.builder().color(TextColor.GREEN).append(TextComponent.of(data.toString())).build();
    }

    @Override
    public TextComponent fail(TData data) {
        return TextComponent.builder().color(TextColor.RED).append(TextComponent.of(data.toString())).build();
    }
}
