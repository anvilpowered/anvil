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

package rocks.milspecsg.msrepository.service.sponge.tools.resultbuilder;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;

import java.util.ArrayList;
import java.util.List;

public class SpongeStringResult extends SpongeResult<String> implements StringResult<Text> {
    
    @Override
    public Builder<Text> builder() {
        return new SpongeStringResultBuilder();
    }

    private static final class SpongeStringResultBuilder implements Builder<Text> {
        
        private final List<TextElement> elements;

        private SpongeStringResultBuilder() {
            elements = new ArrayList<>();
        }

        @Override
        public Builder<Text> aqua() {
            elements.add(TextColors.AQUA);
            return this;
        }

        @Override
        public Builder<Text> black() {
            elements.add(TextColors.BLACK);
            return this;
        }

        @Override
        public Builder<Text> blue() {
            elements.add(TextColors.BLUE);
            return this;
        }

        @Override
        public Builder<Text> dark_aqua() {
            elements.add(TextColors.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<Text> dark_blue() {
            elements.add(TextColors.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<Text> dark_gray() {
            elements.add(TextColors.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<Text> dark_green() {
            elements.add(TextColors.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<Text> dark_purple() {
            elements.add(TextColors.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<Text> dark_red() {
            elements.add(TextColors.DARK_RED);
            return this;
        }

        @Override
        public Builder<Text> gold() {
            elements.add(TextColors.GOLD);
            return this;
        }

        @Override
        public Builder<Text> gray() {
            elements.add(TextColors.GRAY);
            return this;
        }

        @Override
        public Builder<Text> green() {
            elements.add(TextColors.GREEN);
            return this;
        }

        @Override
        public Builder<Text> light_purple() {
            elements.add(TextColors.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<Text> red() {
            elements.add(TextColors.RED);
            return this;
        }

        @Override
        public Builder<Text> reset() {
            elements.add(TextColors.RESET);
            return this;
        }

        @Override
        public Builder<Text> white() {
            elements.add(TextColors.WHITE);
            return this;
        }

        @Override
        public Builder<Text> yellow() {
            elements.add(TextColors.YELLOW);
            return this;
        }
        @Override
        public Builder<Text> append(Object... content) {
            elements.add(Text.of(content));
            return this;
        }

        @Override
        public Text build() {
            return Text.of(elements.toArray());
        }
    }
}
