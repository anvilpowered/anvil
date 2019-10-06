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

package rocks.milspecsg.msrepository;


import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class PluginInfo {
    public static final String Id = "msrepositorytest";
    public static final String Name = "MSRepsitoryTest";
    public static final String Version = "0.1.0-dev";
    public static final String Description = "MSRepository plugin api";
    public static final String Url = "https://milspecsg.rocks";
    public static final String Authors = "Cableguy20";
    public static final Text PluginPrefix = Text.of(TextColors.GREEN, "[MSRepositoryTest] ");
}
