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

package rocks.milspecsg.msrepository.common.data.config;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import rocks.milspecsg.msrepository.api.data.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.data.key.Key;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.api.data.registry.RegistryLoadedListener;
import rocks.milspecsg.msrepository.common.data.registry.CommonRegistry;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Service to load and save data from a config file
 *
 * @author Cableguy20
 */
@Singleton
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class CommonConfigurationService extends CommonRegistry implements ConfigurationService {

    protected ConfigurationLoader<CommentedConfigurationNode> configLoader;
    protected CommentedConfigurationNode rootConfigurationNode;

    /**
     * Maps Keys to their verification function
     */
    protected Map<Key<?>, Map<Predicate<Object>, Function<Object, Object>>> verificationMap;

    /**
     * Maps ConfigKeys to configuration node names
     */
    protected Map<Key<?>, String> nodeNameMap;

    /**
     * Maps ConfigKeys to configuration node descriptions
     */
    protected Map<Key<?>, String> nodeDescriptionMap;

    private boolean configValuesEdited;

    @Inject
    public CommonConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        this.configLoader = configLoader;

        verificationMap = new HashMap<>();
        initVerificationMap();

        nodeNameMap = new HashMap<>();
        initNodeNameMap();

        nodeDescriptionMap = new HashMap<>();
        initNodeDescriptionMap();
    }

    protected void initVerificationMap() {
    }

    protected void initNodeNameMap() {
        nodeNameMap.put(Keys.USE_SHARED_ENVIRONMENT, "datastore.useSharedEnvironment");
        nodeNameMap.put(Keys.USE_SHARED_CREDENTIALS, "datastore.useSharedCredentials");
        nodeNameMap.put(Keys.DATA_STORE_NAME, "datastore.dataStoreName");
        nodeNameMap.put(Keys.MONGODB_HOSTNAME, "datastore.mongodb.hostname");
        nodeNameMap.put(Keys.MONGODB_PORT, "datastore.mongodb.port");
        nodeNameMap.put(Keys.MONGODB_DBNAME, "datastore.mongodb.dbname");
        nodeNameMap.put(Keys.MONGODB_USERNAME, "datastore.mongodb.username");
        nodeNameMap.put(Keys.MONGODB_PASSWORD, "datastore.mongodb.password");
        nodeNameMap.put(Keys.MONGODB_AUTH_DB, "datastore.mongodb.authDb");
        nodeNameMap.put(Keys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth");
    }

    protected void initNodeDescriptionMap() {
        nodeDescriptionMap.put(Keys.USE_SHARED_ENVIRONMENT, "\nWhether to use MSCore shared environment." +
            "\nThis will use hostname and port from MSCore");
        nodeDescriptionMap.put(Keys.USE_SHARED_CREDENTIALS, "\nWhether to use MSCore credentials. (Requires useSharedEnvironment)" +
            "\nThis will use (additionally) username, password, authDb and useAuth from MSCore");
        nodeDescriptionMap.put(Keys.DATA_STORE_NAME, "\nDetermines which storage option to use");
        nodeDescriptionMap.put(Keys.MONGODB_HOSTNAME, "\nMongoDB hostname");
        nodeDescriptionMap.put(Keys.MONGODB_PORT, "\nMongoDB port");
        nodeDescriptionMap.put(Keys.MONGODB_DBNAME, "\nMongoDB database name");
        nodeDescriptionMap.put(Keys.MONGODB_USERNAME, "\nMongoDB username");
        nodeDescriptionMap.put(Keys.MONGODB_PASSWORD, "\nMongoDB password");
        nodeDescriptionMap.put(Keys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication");
        nodeDescriptionMap.put(Keys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
    }

    @Override
    public <T> void set(Key<T> key, T value) {
        super.set(key, value);
        configValuesEdited = true;
    }

    @Override
    public <T> void add(Key<? extends Collection<T>> key, T value) {
        super.add(key, value);
        configValuesEdited = true;
    }

    @Override
    public <K, T> void put(Key<? extends Map<K, T>> key, K mapKey, T value) {
        super.put(key, mapKey, value);
        configValuesEdited = true;
    }

    @Override
    public void load(Object plugin) {
        initConfigMaps();
        super.load(plugin);
    }

    @Override
    public boolean save() {
        if (configValuesEdited) {
            for (Map.Entry<Key<?>, String> entry : nodeNameMap.entrySet()) {
                CommentedConfigurationNode node = fromString(entry.getValue());
                node.setValue(valueMap.get(entry.getKey()));
            }
            try {
                configLoader.save(rootConfigurationNode);
                configValuesEdited = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void notifyConfigLoadedListeners(Object plugin) {
        registryLoadedListeners.forEach(listener -> listener.loaded(plugin));
    }

    @Override
    public void addRegistryLoadedListener(RegistryLoadedListener registryLoadedListener) {
        registryLoadedListeners.add(registryLoadedListener);
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

        try {
            rootConfigurationNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int updatedCount = 0;
        for (Map.Entry<Key<?>, String> entry : nodeNameMap.entrySet()) {
            Key<?> nodeKey = entry.getKey();
            CommentedConfigurationNode node = fromString(entry.getValue());
            if (node.isVirtual()) {
                node.setValue(getDefault(nodeKey));
                updatedCount++;
            } else {
                boolean[] modified = {false};
                initConfigValue(nodeKey, node, modified);
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
            try {
                configLoader.save(rootConfigurationNode);
                configValuesEdited = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param typeToken {@link TypeToken} of node to parse. Pass a {@link Key} to save that value to the registry
     * @param node      {@link CommentedConfigurationNode} to get value from
     */
    private <T> T initConfigValue(TypeToken<T> typeToken, CommentedConfigurationNode node, boolean[] modified) {

        // it ain't pretty but it works

        if (typeToken instanceof Key && typeToken.isSubtypeOf(List.class)) {

            // *** unwrap list *** //
            try {

                Method getMethod = List.class.getMethod("get", int.class);
                Invokable<? extends T, ?> invokable = typeToken.method(getMethod);
                List<?> list = (List<?>) verify(verificationMap.get(typeToken), node.getList(invokable.getReturnType()), node, modified);

                valueMap.put((Key<T>) typeToken, list);

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
                    result.put(entry.getValue().getKey(), initConfigValue(subType, entry.getValue(), modified));
                }

                if (typeToken instanceof Key) {
                    Key<T> key = (Key<T>) typeToken;
                    Map<?, ?> map = (Map<?, ?>) verify(verificationMap.get(key), result, node, modified);
                    valueMap.put(key, map);
                }

                return (T) result;

            } catch (NoSuchMethodException | IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }
        } else if (typeToken instanceof Key) {
            try {
                Object value = node.getValue();
                Key<T> key = (Key<T>) typeToken;
                valueMap.put(key, verify(verificationMap.get(key), value, node, modified));
                return (T) value;
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                return node.getValue(typeToken);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private <T> T verify(Map<Predicate<T>, Function<T, T>> verificationMap, T value, CommentedConfigurationNode node, boolean[] modified) {
        if (verificationMap == null) return value; // if there is no verification function defined
        T result = value;
        for (Map.Entry<Predicate<T>, Function<T, T>> entry : verificationMap.entrySet()) {
            if (entry.getKey().test(result)) {
                modified[0] = true;
                result = entry.getValue().apply(result);
            }
        }
        if (modified[0]) {
            node.setValue(result);
        }
        return result;
    }
}
