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

package rocks.milspecsg.msrepository.service.config;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import rocks.milspecsg.msrepository.api.config.ConfigLoadedListener;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Service to load and save data from a config file
 *
 * @author Cableguy20
 */
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public abstract class ApiConfigurationService implements ConfigurationService {

    protected ConfigurationLoader<CommentedConfigurationNode> configLoader;
    protected CommentedConfigurationNode rootConfigurationNode;

    protected Map<Integer, Boolean> defaultBooleanMap;
    protected Map<Integer, Double> defaultDoubleMap;
    protected Map<Integer, Integer> defaultIntegerMap;
    protected Map<Integer, String> defaultStringMap;
    protected Map<Integer, List<?>> defaultListMap;
    protected Map<Integer, Map<?, ?>> defaultMapMap;

    protected Map<Integer, Boolean> configBooleanMap;
    protected Map<Integer, Double> configDoubleMap;
    protected Map<Integer, Integer> configIntegerMap;
    protected Map<Integer, String> configStringMap;
    protected Map<Integer, List<?>> configListMap;
    protected Map<Integer, Map<?, ?>> configMapMap;

    protected Map<Integer, Map<Predicate<Boolean>, Function<Boolean, Boolean>>> booleanVerificationMap;
    protected Map<Integer, Map<Predicate<Double>, Function<Double, Double>>> doubleVerificationMap;
    protected Map<Integer, Map<Predicate<Integer>, Function<Integer, Integer>>> integerVerificationMap;
    protected Map<Integer, Map<Predicate<String>, Function<String, String>>> stringVerificationMap;
    protected Map<Integer, Map<Predicate<List<?>>, Function<List<?>, List<?>>>> listVerificationMap;
    protected Map<Integer, Map<Predicate<Map<?, ?>>, Function<Map<?, ?>, Map<?, ?>>>> mapVerificationMap;
//    protected Map<Integer, Map<? extends Predicate<? extends Map<?, ?>>, ? extends Function<? extends Map<?, ?>, ? extends Map<?, ?>>>> mapVerificationMap;
    /**
     * Maps ConfigKeys to configuration node names
     */
    protected Map<Integer, String> nodeNameMap;

    /**
     * Maps ConfigKeys to configuration node descriptions
     */
    protected Map<Integer, String> nodeDescriptionMap;

    /**
     * Maps ConfigKeys to configuration node types
     */
    protected Map<Integer, TypeToken<?>> nodeTypeMap;

    /**
     * Stores a list of {@link ConfigLoadedListener} to notify
     */
    private List<ConfigLoadedListener> configLoadedListeners;

    private boolean configValuesEdited = false;

    public ApiConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        configLoadedListeners = new ArrayList<>();
        this.configLoader = configLoader;
        //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Loading service"));

        nodeTypeMap = new HashMap<>();
        initNodeTypeMap();

        booleanVerificationMap = new HashMap<>();
        doubleVerificationMap = new HashMap<>();
        integerVerificationMap = new HashMap<>();
        stringVerificationMap = new HashMap<>();
        listVerificationMap = new HashMap<>();
        mapVerificationMap = new HashMap<>();
        initVerificationMaps();

        defaultBooleanMap = new HashMap<>();
        defaultDoubleMap = new HashMap<>();
        defaultIntegerMap = new HashMap<>();
        defaultStringMap = new HashMap<>();
        defaultListMap = new HashMap<>();
        defaultMapMap = new HashMap<>();
        initDefaultMaps();

        nodeNameMap = new HashMap<>();
        initNodeNameMap();

        nodeDescriptionMap = new HashMap<>();
        initNodeDescriptionMap();

        configBooleanMap = new HashMap<>();
        configDoubleMap = new HashMap<>();
        configIntegerMap = new HashMap<>();
        configStringMap = new HashMap<>();
        configListMap = new HashMap<>();
        configMapMap = new HashMap<>();

        //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Config done"));
    }

    abstract protected void initNodeTypeMap();

    abstract protected void initVerificationMaps();

    abstract protected void initDefaultMaps();

    abstract protected void initNodeNameMap();

    abstract protected void initNodeDescriptionMap();

    public void load(Object plugin) {
        initConfigMaps();
        notifyConfigLoadedListeners(plugin);
    }

    public void save() {
        //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Saving config"));
        if (configValuesEdited) {
            for (Integer nodeKey : nodeNameMap.keySet()) {
                CommentedConfigurationNode node = fromString(nodeNameMap.get(nodeKey));
                //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Loading node " + nodeKey));
                saveConfigValue(nodeKey, node, nodeTypeMap.get(nodeKey));
            }
            configValuesEdited = false;
        }

        try {
            configLoader.save(rootConfigurationNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyConfigLoadedListeners(Object plugin) {
        configLoadedListeners.forEach(listener -> listener.loaded(plugin));
    }

    @Override
    public void addConfigLoadedListener(ConfigLoadedListener configLoadedListener) {
        this.configLoadedListeners.add(configLoadedListener);
    }

    @Override
    public void removeConfigLoadedListener(ConfigLoadedListener configLoadedListener) {
        this.configLoadedListeners.remove(configLoadedListener);
    }

    private CommentedConfigurationNode fromString(String name) {
        String[] path = name.split("[.]");
        CommentedConfigurationNode node = rootConfigurationNode;
        for (String s : path) {
            node = node.getNode(s);
        }
        return node;
    }

    private void initConfigMaps() {
        configBooleanMap = new HashMap<>();
        configDoubleMap = new HashMap<>();
        configIntegerMap = new HashMap<>();
        configStringMap = new HashMap<>();
        configListMap = new HashMap<>();
        configMapMap = new HashMap<>();

        try {
            rootConfigurationNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int updatedCount = 0;
        for (Integer nodeKey : nodeNameMap.keySet()) {
            CommentedConfigurationNode node = fromString(nodeNameMap.get(nodeKey));
            //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Loading node " + nodeKey));
            if (node.isVirtual()) {
                saveDefaultValue(nodeKey, node, nodeTypeMap.get(nodeKey));
                updatedCount++;
            } else {
                boolean[] modified = new boolean[]{false};
                initConfigValue(nodeKey, node, nodeTypeMap.get(nodeKey), modified);
                if (modified[0]) {
                    updatedCount++;
                }
            }

            if (node.isVirtual() || !node.getComment().isPresent()) {
                node.setComment(nodeDescriptionMap.get(nodeKey));
                updatedCount++;
            }
        }
        if (updatedCount > 0) {
          save();
        }
    }

    protected <T> void saveConfigValue(Integer nodeKey, CommentedConfigurationNode node, TypeToken<T> typeToken) {
        getConfig(nodeKey, typeToken).ifPresent(node::setValue);
    }

    protected <T> void saveDefaultValue(Integer nodeKey, CommentedConfigurationNode node, TypeToken<T> typeToken) {
        getDefault(nodeKey, typeToken).ifPresent(node::setValue);
    }

    /**
     * @param nodeKey   {@link Integer} key of node
     * @param node      {@link CommentedConfigurationNode} to get value from
     * @param typeToken {@link TypeToken} representing the type of node. Use {@link #nodeTypeMap}
     */
    protected <T> T initConfigValue(Integer nodeKey, CommentedConfigurationNode node, TypeToken<? extends T> typeToken, boolean[] modified) {

        if (typeToken == null) {
            throw new IllegalStateException("NodeTypeKey " + nodeKey + " does not exist. This needs to be added in your implementation of ApiConfigurationService!");
        }

        // it ain't pretty but it works

        if (typeToken.isSubtypeOf(List.class)) {

            // *** unwrap list *** //
            try {

                Method getMethod = List.class.getMethod("get", int.class);
                Invokable<? extends T, ?> invokable = typeToken.method(getMethod);
                List<?> list = verify(listVerificationMap.get(nodeKey), node.getList(invokable.getReturnType()), node, modified);

                if (nodeKey != null) configListMap.put(nodeKey, list);

                return (T) list;

            } catch (NoSuchMethodException | IllegalArgumentException | ObjectMappingException e) {
                e.printStackTrace();
                return null;
            }

        } else if (typeToken.isSubtypeOf(Map.class)) {

            // *** unwrap map *** //
            try {

                Method getMethod = Map.class.getMethod("get", Object.class);
                Invokable<?, ?> invokable = typeToken.method(getMethod);
                TypeToken<?> subType = invokable.getReturnType();

                Map<Object, Object> result = new HashMap<>();

                for (Map.Entry<?, ? extends CommentedConfigurationNode> entry : node.getChildrenMap().entrySet()) {
                    // here comes the recursion
                    result.put(entry.getValue().getKey(), initConfigValue(null, entry.getValue(), subType, modified));
                }

                if (nodeKey != null) configMapMap.put(nodeKey, result);

                return (T) result;

            } catch (NoSuchMethodException | IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                if (typeToken.isSubtypeOf(Boolean.class)) {
                    Boolean value = node.getBoolean();
                    if (nodeKey != null)
                        configBooleanMap.put(nodeKey, verify(booleanVerificationMap.get(nodeKey), value, node, modified));
                    return (T) value;

                } else if (typeToken.isSubtypeOf(Double.class)) {
                    Double value = node.getDouble();
                    if (nodeKey != null)
                        configDoubleMap.put(nodeKey, verify(doubleVerificationMap.get(nodeKey), value, node, modified));
                    return (T) value;

                } else if (typeToken.isSubtypeOf(Integer.class)) {
                    Integer value = node.getInt();
                    if (nodeKey != null)
                        configIntegerMap.put(nodeKey, verify(integerVerificationMap.get(nodeKey), value, node, modified));
                    return (T) value;

                } else if (typeToken.isSubtypeOf(String.class)) {
                    String value = node.getString();
                    if (nodeKey != null)
                        configStringMap.put(nodeKey, verify(stringVerificationMap.get(nodeKey), value, node, modified));
                    return (T) value;

                } else {
                    throw new Exception("Class did not match any values");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    private <T> T verify(Map<Predicate<T>, Function<T, T>> verificationMap, T value, CommentedConfigurationNode node, boolean[] modified) {
        if (verificationMap == null) return value; // if there is no verification function defined
        for (Map.Entry<Predicate<T>, Function<T, T>> entry : verificationMap.entrySet())
            if (entry.getKey().test(value)) {
                modified[0] = true;
                T result = entry.getValue().apply(value);
                // Sponge.getServer().getConsole().sendMessage(Text.of("Changing ", value, " to ", result));
                node.setValue(result);
                return result;
            }
        return value;
    }

    @Override
    public Optional<?> getDefault(int key) {
        TypeToken<?> typeToken = nodeTypeMap.get(key);
        Optional<?> optionalObject = getDefault(key, typeToken);
        if (optionalObject.isPresent() && typeToken.isSubtypeOf(optionalObject.get().getClass())) {
            return Optional.of(typeToken.getRawType().cast(optionalObject.get()));
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<? extends T> getDefault(int key, TypeToken<T> typeToken) {
        try {
            if (typeToken.isSubtypeOf(Boolean.class)) {
                return Optional.ofNullable((T) getDefaultBoolean(key));
            } else if (typeToken.isSubtypeOf(Double.class)) {
                return Optional.ofNullable((T) getDefaultDouble(key));
            } else if (typeToken.isSubtypeOf(Integer.class)) {
                return Optional.ofNullable((T) getDefaultInteger(key));
            } else if (typeToken.isSubtypeOf(String.class)) {
                return Optional.ofNullable((T) getDefaultString(key));
            } else if (typeToken.isSubtypeOf(List.class)) {
                return Optional.ofNullable((T) getDefaultList(key));
            } else if (typeToken.isSubtypeOf(Map.class)) {
                return Optional.ofNullable((T) getDefaultMap(key));
            } else {
                throw new Exception("Class did not match any values");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<? extends T> getConfig(int key, TypeToken<T> typeToken) {
        try {
            if (typeToken.isSubtypeOf(Boolean.class)) {
                return Optional.of((T) getConfigBoolean(key));
            } else if (typeToken.isSubtypeOf(Double.class)) {
                return Optional.of((T) getConfigDouble(key));
            } else if (typeToken.isSubtypeOf(Integer.class)) {
                return Optional.of((T) getConfigInteger(key));
            } else if (typeToken.isSubtypeOf(String.class)) {
                return Optional.of((T) getConfigString(key));
            } else if (typeToken.isSubtypeOf(List.class)) {
                return Optional.of((T) getConfigList(key));
            } else if (typeToken.isSubtypeOf(Map.class)) {
                return Optional.of((T) getConfigMap(key));
            } else {
                throw new Exception("Class did not match any values");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Boolean getDefaultBoolean(int key) {
        return defaultBooleanMap.get(key);
    }

    @Override
    public Double getDefaultDouble(int key) {
        return defaultDoubleMap.get(key);
    }

    @Override
    public Integer getDefaultInteger(int key) {
        return defaultIntegerMap.get(key);
    }

    @Override
    public String getDefaultString(int key) {
        return defaultStringMap.get(key);
    }

    @Override
    public List<?> getDefaultList(int key) {
        return Collections.unmodifiableList(defaultListMap.get(key));
    }

    @Override
    public <T extends List<?>> T getDefaultList(int key, TypeToken<T> typeToken) {
        return (T) Collections.unmodifiableList(getAssertedType(key, typeToken, k -> defaultListMap.get(key)));

    }

    @Override
    public Map<?, ?> getDefaultMap(int key) {
        return Collections.unmodifiableMap(defaultMapMap.get(key));
    }

    @Override
    public <T extends Map<?, ?>> T getDefaultMap(int key, TypeToken<T> typeToken) {
        return (T) Collections.unmodifiableMap(getAssertedType(key, typeToken, k -> defaultMapMap.get(key)));
    }

    @Override
    public Boolean getConfigBoolean(int key) {
        return getValue(configBooleanMap, defaultBooleanMap, key);
    }

    @Override
    public Double getConfigDouble(int key) {
        return getValue(configDoubleMap, defaultDoubleMap, key);
    }

    @Override
    public Integer getConfigInteger(int key) {
        return getValue(configIntegerMap, defaultIntegerMap, key);
    }

    @Override
    public String getConfigString(int key) {
        return getValue(configStringMap, defaultStringMap, key);
    }

    @Override
    public List<?> getConfigList(int key) {
        return Collections.unmodifiableList(getValue(configListMap, defaultListMap, key));
    }

    @Override
    public <T extends List<?>> T getConfigList(int key, TypeToken<T> typeToken) {
        return (T) Collections.unmodifiableList(getAssertedType(key, typeToken, this::getConfigList));
    }

    @Override
    public Map<?, ?> getConfigMap(int key) {
        return Collections.unmodifiableMap(getValue(configMapMap, defaultMapMap, key));
    }

    @Override
    public <T extends Map<?, ?>> T getConfigMap(int key, TypeToken<T> typeToken) {
        return (T) Collections.unmodifiableMap(getAssertedType(key, typeToken, this::getConfigMap));
    }

    /**
     * @param configMap  Map of values to check if present
     * @param defaultMap Fallback map
     * @param key        configuration node key to use
     * @param <T>        Value type of map
     * @return value at key from configMap if present and non-null, otherwise value at key from defaultMap
     */
    protected <T> T getValue(Map<Integer, T> configMap, Map<Integer, T> defaultMap, int key) {
        if (configMap.containsKey(key)) {
            T result = configMap.get(key);
            return result != null ? result : defaultMap.get(key);
        } else return defaultMap.get(key);
    }

    protected <T> T getAssertedType(int key, TypeToken<T> typeToken, Function<Integer, ?> getter) {

        boolean a = nodeTypeMap.containsKey(key);
        boolean b = nodeTypeMap.get(key).isSupertypeOf(typeToken);

        if (a && b) {
            return (T) getter.apply(key);
        } else if (a) {
            System.err.println("Asserted type mismatch");
            System.err.println("Type in map: " + nodeTypeMap.get(key).toString());
            System.err.println("Asserted type: " + typeToken.toString());
            throw new IllegalArgumentException("Invalid TypeToken or TypeToken does not match stored value for key: " + key + ", name: " + nodeNameMap.getOrDefault(key, "(no name)"));
        } else {
            throw new IllegalStateException("NodeTypeKey " + key + " does not exist. This needs to be added in your implementation of ApiConfigurationService!");
        }
    }

    @Override
    public void setConfigBoolean(int key, Boolean value) {
        configBooleanMap.put(key, value);
        configValuesEdited = true;
    }

    @Override
    public void setConfigDouble(int key, Double value) {
        configDoubleMap.put(key, value);
        configValuesEdited = true;
    }

    @Override
    public void setConfigInteger(int key, Integer value) {
        configIntegerMap.put(key, value);
        configValuesEdited = true;
    }

    @Override
    public void setConfigString(int key, String value) {
        configStringMap.put(key, value);
        configValuesEdited = true;
    }

    @Override
    public void setConfigList(int key, List<?> value) {
        configListMap.put(key, value);
        configValuesEdited = true;
    }

    @Override
    public <T, L extends List<T>> void addToConfigList(int key, T value, TypeToken<L> typeToken) {
        ((L) configListMap.get(key)).add(value);
        configValuesEdited = true;
    }

    @Override
    public boolean removeFromConfigList(int key, Object value) {
        configValuesEdited = true;
        return configListMap.get(key).remove(value);
    }

    @Override
    public void setConfigMap(int key, Map<?, ?> map) {
        configMapMap.put(key, map);
        configValuesEdited = true;
    }

    @Override
    public <K, T, M extends Map<K, T>> void addToConfigMap(int key, K mapKey, T value, TypeToken<M> typeToken) {
        ((M) configMapMap.get(key)).put(mapKey, value);
        configValuesEdited = true;
    }

    @Override
    public <K, T, M extends Map<K, T>> T removeFromConfigMap(int key, K mapKey, TypeToken<M> typeToken) {
        configValuesEdited = true;
        return ((M) configMapMap.get(key)).remove(mapKey);
    }

    @Override
    public TypeToken<?> getType(int key) {
        return nodeTypeMap.get(key);
    }
}
