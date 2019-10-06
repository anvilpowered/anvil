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

package rocks.milspecsg.msrepository.datastore.json;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonDataStore {













    public static ConfigurationLoader<ConfigurationNode> getLoader(Path path, String name) {
        Path configPath = Paths.get(path + "/" + name + ".json");
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(path);
                Files.createFile(configPath);
            }
            return GsonConfigurationLoader.builder()
                .setPath(configPath)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
