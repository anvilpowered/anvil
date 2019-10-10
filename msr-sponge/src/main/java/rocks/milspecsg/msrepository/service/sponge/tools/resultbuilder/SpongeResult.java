/*
 *     MSParties - MilSpecSG
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

package rocks.milspecsg.msrepository.service.sponge.tools.resultbuilder;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.Result;

public class SpongeResult<TData> implements Result<Text, TData> {

    @Override
    public Text success(TData data) {
        return Text.of(TextColors.GREEN, data);
    }

    @Override
    public Text fail(TData data) {
        return Text.of(TextColors.RED, data);
    }
}
