package rocks.milspecsg.msrepository.api.config;

import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfigurationService {

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
    <T> List<T> getDefaultList(int key, TypeToken<T> typeToken);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Map<String, ?> getDefaultMap(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T> Map<String, T> getDefaultMap(int key, TypeToken<T> typeToken);

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
    <T> List<T> getConfigList(int key, TypeToken<T> typeToken);

    /**
     * @param key corresponds to {@link ConfigKeys} final ints
     * @return default value for this key
     */
    Map<String, ?> getConfigMap(int key);

    /**
     * @param key       corresponds to {@link ConfigKeys} final ints
     * @param typeToken {@link TypeToken} of value
     * @return default value for this key
     */
    <T> Map<String, T> getConfigMap(int key, TypeToken<T> typeToken);


}
