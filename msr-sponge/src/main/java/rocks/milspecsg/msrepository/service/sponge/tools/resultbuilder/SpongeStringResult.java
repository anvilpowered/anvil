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
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;

public class SpongeStringResult extends SpongeResult<String> implements StringResult<Text> {
    
    @Override
    public Builder<Text> builder() {
        return new SpongeStringResultBuilder();
    }

    private static class SpongeStringResultBuilder implements Builder<Text> {
        
        private final Text.Builder builder;

        private SpongeStringResultBuilder() {
            builder = Text.builder();
        }

        @Override
        public Builder<Text> aqua() {
            builder.append(Text.of(TextColors.AQUA));
            return this;
        }

        @Override
        public Builder<Text> black() {
            builder.append(Text.of(TextColors.BLACK));
            return this;
        }

        @Override
        public Builder<Text> blue() {
            builder.append(Text.of(TextColors.BLUE));
            return this;
        }

        @Override
        public Builder<Text> dark_aqua() {
            builder.append(Text.of(TextColors.DARK_AQUA));
            return this;
        }

        @Override
        public Builder<Text> dark_blue() {
            builder.append(Text.of(TextColors.DARK_BLUE));
            return this;
        }

        @Override
        public Builder<Text> dark_gray() {
            builder.append(Text.of(TextColors.DARK_GRAY));
            return this;
        }

        @Override
        public Builder<Text> dark_green() {
            builder.append(Text.of(TextColors.DARK_GREEN));
            return this;
        }

        @Override
        public Builder<Text> dark_purple() {
            builder.append(Text.of(TextColors.DARK_PURPLE));
            return this;
        }

        @Override
        public Builder<Text> dark_red() {
            builder.append(Text.of(TextColors.DARK_RED));
            return this;
        }

        @Override
        public Builder<Text> gold() {
            builder.append(Text.of(TextColors.GOLD));
            return this;
        }

        @Override
        public Builder<Text> gray() {
            builder.append(Text.of(TextColors.GRAY));
            return this;
        }

        @Override
        public Builder<Text> green() {
            builder.append(Text.of(TextColors.GREEN));
            return this;
        }

        @Override
        public Builder<Text> light_purple() {
            builder.append(Text.of(TextColors.LIGHT_PURPLE));
            return this;
        }

        @Override
        public Builder<Text> red() {
            builder.append(Text.of(TextColors.RED));
            return this;
        }

        @Override
        public Builder<Text> reset() {
            builder.append(Text.of(TextColors.RESET));
            return this;
        }

        @Override
        public Builder<Text> white() {
            builder.append(Text.of(TextColors.WHITE));
            return this;
        }

        @Override
        public Builder<Text> yellow() {
            builder.append(Text.of(TextColors.YELLOW));
            return this;
        }
        @Override
        public Builder<Text> append(Object... content) {
            builder.append(Text.of(content));
            return this;
        }

        @Override
        public Text build() {
            return builder.build();
        }
    }
}
