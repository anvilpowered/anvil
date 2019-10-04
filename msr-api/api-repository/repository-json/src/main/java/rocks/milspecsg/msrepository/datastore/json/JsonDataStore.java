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
