package rocks.milspecsg.msrepository.api.config;

import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfigurationService {

    /**
     * Loads config values
     * Must be run
     */
    void load(Object plugin);

    void save();

    void addConfigLoadedListener(ConfigLoadedListener configLoadedListener);

    void removeConfigLoadedListener(ConfigLoadedListener configLoadedListener);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Optional<?> getDefault(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T> Optional<? extends T> getDefault(int key, TypeToken<T> typeToken);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return config value for this key
     */
    <T> Optional<? extends T> getConfig(int key, TypeToken<T> typeToken);

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
    List<?> getDefaultList(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T extends List<?>> T getDefaultList(int key, TypeToken<T> typeToken);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Map<?, ?> getDefaultMap(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T extends Map<?, ?>> T getDefaultMap(int key, TypeToken<T> typeToken);

//    /**
//     * Will provide default values if the ones from the service are not present
//     *
//     * @param key corresponds to {@link ConfigKeys} final ints
//     * @return service value for this key
//     */
//    Optional<?> getConfig(int key);

    /**
     * Will provide default values if the ones from the service are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return service value for this key
     */
    Boolean getConfigBoolean(int key);

    /**
     * Will provide default values if the ones from the service are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return service value for this key
     */
    Double getConfigDouble(int key);

    /**
     * Will provide default values if the ones from the service are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return service value for this key
     */
    Integer getConfigInteger(int key);

    /**
     * Will provide default values if the ones from the service are not present
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return service value for this key
     */
    String getConfigString(int key);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    List<?> getConfigList(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T extends List<?>> T getConfigList(int key, TypeToken<T> typeToken);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Map<?, ?> getConfigMap(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T extends Map<?, ?>> T getConfigMap(int key, TypeToken<T> typeToken);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    void setConfigBoolean(int key, Boolean value);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    void setConfigDouble(int key, Double value);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    void setConfigInteger(int key, Integer value);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    void setConfigString(int key, String value);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    void setConfigList(int key, List<?> value);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    <T, L extends List<T>> void addToConfigList(int key, T value, TypeToken<L> typeToken);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return whether the key was successfully removed
     */
    boolean removeFromConfigList(int key, Object value);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    void setConfigMap(int key, Map<?, ?> map);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    <K, T, M extends Map<K, T>> void addToConfigMap(int key, K mapKey, T value, TypeToken<M> typeToken);

    /**
     *
     * @param key corresponds to {@link ConfigKeys} final ints
     */
    <K, T, M extends Map<K, T>> T removeFromConfigMap(int key, K mapKey, TypeToken<M> typeToken);

    TypeToken<?> getType(int key);


}
