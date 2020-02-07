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

package rocks.milspecsg.anvil.core.velocity.module;

import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import rocks.milspecsg.anvil.core.common.module.CommonModule;
import rocks.milspecsg.anvil.core.common.plugin.AnvilCorePluginInfo;

import java.io.File;
import java.nio.file.Paths;

public class VelocityModule extends CommonModule<TextComponent, CommandSource> {

    @Override
    protected void configure() {
        super.configure();
        File configFilesLocation = Paths.get("plugins/" + AnvilCorePluginInfo.id).toFile();
        if (!configFilesLocation.exists()) {
            if (!configFilesLocation.mkdirs()){
                throw new IllegalStateException("Unable to create config directory");
            }
        }
        bind (new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>(){
        }).toInstance(HoconConfigurationLoader.builder().setPath(Paths.get(configFilesLocation + "/anvil.conf")).build());
    }
}
