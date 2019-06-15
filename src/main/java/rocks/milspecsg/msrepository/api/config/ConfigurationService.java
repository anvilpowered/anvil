package rocks.milspecsg.msrepository.api.config;

import java.util.List;
import java.util.Optional;

public interface ConfigurationService {

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Optional<?> getDefault(int key);

    /**
     * @param key   corresponds to {@link ConfigKeys} final ints
     * @param clazz type of value
     * @return default value for this key
     */
    <T> Optional<T> getDefault(int key, Class<T> clazz);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Boolean getDefaultBoolean(int key);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Double getDefaultDouble(int key);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Integer getDefaultInteger(int key);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    String getDefaultString(int key);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    List<String> getDefaultStringList(int key);

    /**
     * Will provide default values if the ones from the config are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return config value for this key
     */
    <T> T getConfig(int key);

    /**
     * Will provide default values if the ones from the config are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return config value for this key
     */
    Boolean getConfigBoolean(int key);

    /**
     * Will provide default values if the ones from the config are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return config value for this key
     */
    Double getConfigDouble(int key);

    /**
     * Will provide default values if the ones from the config are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return config value for this key
     */
    Integer getConfigInteger(int key);

    /**
     * Will provide default values if the ones from the config are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return config value for this key
     */
    String getConfigString(int key);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    List<String> getConfigStringList(int key);

}
