/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.base.registry;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.anvilpowered.anvil.api.registry.ConfigurationService;
import org.anvilpowered.anvil.api.registry.Key;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.RegistryScope;
import org.anvilpowered.anvil.api.registry.RegistryScoped;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Service to load and save data from a config file
 *
 * @author Cableguy20
 */
@Singleton
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class BaseConfigurationService extends BaseRegistry implements ConfigurationService {

    protected ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode rootConfigurationNode;

    /**
     * Maps Keys to their verification function
     */
    private final Map<Key<?>, Map<Predicate<Object>, Function<Object, Object>>> verificationMap;

    /**
     * Maps ConfigKeys to configuration node names
     */
    private final Map<Key<?>, String> nodeNameMap;

    /**
     * Maps ConfigKeys to configuration node descriptions
     */
    private final Map<Key<?>, String> nodeDescriptionMap;

    private boolean configValuesEdited;
    private boolean isWithDataStore = false;
    @Nullable
    private ConfigurationOptions options;

    @Inject
    public BaseConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        this.configLoader = configLoader;
        verificationMap = new HashMap<>();
        nodeNameMap = new HashMap<>();
        nodeDescriptionMap = new HashMap<>();
    }

    protected void setOptions(@Nullable ConfigurationOptions options) {
        this.options = options;
    }

    protected void withCore() {
        setName(Keys.SERVER_NAME, "server.name");
        setDescription(Keys.SERVER_NAME, "\nServer name");
    }

    private void withDataStoreCore0() {
        setName(Keys.DATA_DIRECTORY, "datastore.dataDirectory");
        setName(Keys.DATA_STORE_NAME, "datastore.dataStoreName");
        setDescription(Keys.DATA_DIRECTORY, "\nDirectory for extra data" +
            "\nPlease note that it is not recommended to change this value from the original");
        setDescription(Keys.DATA_STORE_NAME, "\nDetermines which storage option to use");
    }

    protected void withDataStoreCore() {
        if (isWithDataStore) return;
        isWithDataStore = true;
        withDataStoreCore0();
    }

    protected void withDataStore() {
        if (isWithDataStore) return;
        isWithDataStore = true;
        withDataStoreCore0();
        setName(Keys.USE_SHARED_CREDENTIALS, "datastore.anvil.useSharedCredentials");
        setName(Keys.USE_SHARED_ENVIRONMENT, "datastore.anvil.useSharedEnvironment");
        setDescription(Keys.USE_SHARED_CREDENTIALS, "\nWhether to use Anvil's shared credentials."
            + "\nIf enabled, the following datastore settings will be inherited from Anvil's config (Requires useSharedEnvironment)"
            + "\n\t- mongodb.authDb"
            + "\n\t- mongodb.connectionString"
            + "\n\t- mongodb.password"
            + "\n\t- mongodb.username"
            + "\n\t- mongodb.useAuth"
            + "\n\t- mongodb.useConnectionString"
            + "\n\t- mongodb.useSrv"
            + "\nPlease note: If this is enabled, the values for above settings in this config file have no effect"
        );
        setDescription(Keys.USE_SHARED_ENVIRONMENT, "\nWhether to use Anvil's shared environment."
            + "\nIf enabled, the following datastore settings will be inherited from Anvil's config"
            + "\n\t- mongodb.hostname"
            + "\n\t- mongodb.port"
            + "\nPlease note: If this is enabled, the values for above settings in this config file have no effect"
        );
    }

    protected void withMongoDB() {
        withDataStore();
        setName(Keys.MONGODB_CONNECTION_STRING, "datastore.mongodb.connectionString");
        setName(Keys.MONGODB_HOSTNAME, "datastore.mongodb.hostname");
        setName(Keys.MONGODB_PORT, "datastore.mongodb.port");
        setName(Keys.MONGODB_DBNAME, "datastore.mongodb.dbname");
        setName(Keys.MONGODB_USERNAME, "datastore.mongodb.username");
        setName(Keys.MONGODB_PASSWORD, "datastore.mongodb.password");
        setName(Keys.MONGODB_AUTH_DB, "datastore.mongodb.authDb");
        setName(Keys.MONGODB_USE_AUTH, "datastore.mongodb.useAuth");
        setName(Keys.MONGODB_USE_SRV, "datastore.mongodb.useSrv");
        setName(Keys.MONGODB_USE_CONNECTION_STRING, "datastore.mongodb.useConnectionString");
        setDescription(Keys.MONGODB_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nCustom MongoDB connection string that will used instead of the connection info and credentials below" +
            "\nWill only be used if useConnectionString=true");
        setDescription(Keys.MONGODB_HOSTNAME, "\nMongoDB hostname");
        setDescription(Keys.MONGODB_PORT, "\nMongoDB port");
        setDescription(Keys.MONGODB_DBNAME, "\nMongoDB database name");
        setDescription(Keys.MONGODB_USERNAME, "\nMongoDB username");
        setDescription(Keys.MONGODB_PASSWORD, "\nMongoDB password");
        setDescription(Keys.MONGODB_AUTH_DB, "\nMongoDB database to use for authentication");
        setDescription(Keys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
        setDescription(Keys.MONGODB_USE_SRV, "\nWhether to interpret the MongoDB hostname as an SRV record");
        setDescription(Keys.MONGODB_USE_CONNECTION_STRING, "\n(Advanced) You will probably not need to use this." +
            "\nWhether to use the connection string provided instead of the normal connection info and credentials" +
            "\nOnly use this if you know what you are doing!" +
            "\nPlease note that this plugin will inherit both useConnectionString and connectionString from" +
            "\nAnvil if and only if useSharedEnvironment and useSharedCredentials are both true");
    }

    protected void withRedis() {
        setName(Keys.REDIS_HOSTNAME, "datastore.redis.hostname");
        setName(Keys.REDIS_PORT, "datastore.redis.port");
        setName(Keys.REDIS_PASSWORD, "datastore.redis.password");
        setName(Keys.REDIS_USE_AUTH, "datastore.redis.useAuth");
        setDescription(Keys.REDIS_HOSTNAME, "\nRedis hostname");
        setDescription(Keys.REDIS_PORT, "\nRedis Port");
        setDescription(Keys.REDIS_PASSWORD, "\nRedis password");
        setDescription(Keys.REDIS_USE_AUTH, "\nWhether to use authentication (password) for Redis connection");
    }

    protected void withProxyMode() {
        setName(Keys.PROXY_MODE, "server.proxyMode");
        setDescription(Keys.PROXY_MODE, "\nEnable this if your server is running behind a proxy"
            + "\nIf true, this setting disables the join and chat listeners"
            + "\nto prevent conflicts with the proxy's listeners.");
    }

    protected void withDefault() {
        withCore();
        withMongoDB();
    }

    protected void withAll() {
        withDefault();
        withRedis();
        withProxyMode();
    }

    protected <T> void setVerification(Key<T> key,
                                       Map<Predicate<T>, Function<T, T>> verification) {
        verificationMap.put(key,
            (Map<Predicate<Object>, Function<Object, Object>>) (Object) verification);
    }

    protected void setName(Key<?> key, String name) {
        nodeNameMap.put(key, name);
    }

    protected void setDescription(Key<?> key, String description) {
        nodeDescriptionMap.put(key, description);
    }

    @Override
    public <T> void set(Key<T> key, T value) {
        super.set(key, value);
        configValuesEdited = true;
    }

    @Override
    public <T> void remove(Key<T> key) {
        super.remove(key);
        configValuesEdited = true;
    }

    @Override
    public <T> void transform(Key<T> key, BiFunction<? super Key<T>, ? super T, ? extends T> transformer) {
        super.transform(key, transformer);
        configValuesEdited = true;
    }

    @Override
    public <T> void transform(Key<T> key, Function<? super T, ? extends T> transformer) {
        super.transform(key, transformer);
        configValuesEdited = true;
    }

    @Override
    public <T> void addToCollection(Key<? extends Collection<T>> key, T value) {
        super.addToCollection(key, value);
        configValuesEdited = true;
    }

    @Override
    public <T> void removeFromCollection(Key<? extends Collection<T>> key, T value) {
        super.removeFromCollection(key, value);
        configValuesEdited = true;
    }

    @Override
    public <K, T> void putInMap(Key<? extends Map<K, T>> key, K mapKey, T mapValue) {
        super.putInMap(key, mapKey, mapValue);
        configValuesEdited = true;
    }

    @Override
    public <K, T> void removeFromMap(Key<? extends Map<K, T>> key, K mapKey) {
        super.removeFromMap(key, mapKey);
        configValuesEdited = true;
    }

    @Override
    public void load(RegistryScope registryScope) {
        final int ordinal = registryScope.ordinal();
        if (ordinal <= RegistryScope.DEFAULT.ordinal()) {
            loadDefaultScope();
        }
        loadConfig();
        loadOrdinal(ordinal);
    }

    @Override
    public boolean save() {
        if (configValuesEdited) {
            for (Map.Entry<Key<?>, String> entry : nodeNameMap.entrySet()) {
                CommentedConfigurationNode node = fromString(entry.getValue());
                try {
                    setNodeValue(node, entry.getKey());
                } catch (ObjectMappingException e) {
                    logger.error("Unable to set config value for " + entry.getKey(), e);
                }
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

    private CommentedConfigurationNode fromString(String name) {
        String[] path = name.split("[.]");
        CommentedConfigurationNode node = rootConfigurationNode;
        for (String s : path) {
            node = node.getNode(s);
        }
        return node;
    }

    private <T> void setNodeDefault(CommentedConfigurationNode node, Key<T> key) throws ObjectMappingException {
        T def = getDefault(key);
        node.setValue(key.getType(), def);
        set(key, def);
    }

    private <T> void setNodeValue(CommentedConfigurationNode node, Key<T> key) throws ObjectMappingException {
        node.setValue(key.getType(), getOrDefault(key));
    }

    @RegistryScoped
    private void loadConfig() {
        try {
            if (options == null) {
                rootConfigurationNode = configLoader.load();
            } else {
                rootConfigurationNode = configLoader.load(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int updatedCount = 0;
        for (Map.Entry<Key<?>, String> entry : nodeNameMap.entrySet()) {
            Key<?> key = entry.getKey();
            CommentedConfigurationNode node = fromString(entry.getValue());
            if (node.isVirtual()) {
                try {
                    setNodeDefault(node, key);
                    updatedCount++;
                } catch (ObjectMappingException e) {
                    e.printStackTrace();
                }
            } else {
                boolean[] modified = {false};
                initConfigValue(key, node, modified);
                if (modified[0]) {
                    updatedCount++;
                }
            }

            if (node.isVirtual() || !node.getComment().isPresent()) {
                node.setComment(nodeDescriptionMap.get(key));
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

    @Nullable
    private <T> T initConfigValue(Key<T> key, CommentedConfigurationNode node, boolean[] modified) {
        return initConfigValue(key, key.getType(), node, modified);
    }

    /**
     * @param typeToken {@link TypeToken} of node to parse. Pass a {@link Key} to save that value to the registry
     * @param node      {@link CommentedConfigurationNode} to get value from
     */
    @Nullable
    private <T> T initConfigValue(@Nullable Key<T> key, TypeToken<T> typeToken, CommentedConfigurationNode node, boolean[] modified) {

        // it ain't pretty but it works

        if (key != null && typeToken.isSubtypeOf(List.class)) {

            // *** unwrap list *** //
            try {

                Method getMethod = List.class.getMethod("get", int.class);
                Invokable<? extends T, ?> invokable = typeToken.method(getMethod);
                T list = (T) verify(verificationMap.get(key), node.getList(invokable.getReturnType()), node, modified);

                set(key, list);

                return list;

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
                    result.put(entry.getValue().getKey(), initConfigValue(null, subType, entry.getValue(), modified));
                }

                if (key != null) {
                    T map = (T) verify(verificationMap.get(key), result, node, modified);
                    set(key, map);
                }

                return (T) result;

            } catch (NoSuchMethodException | IllegalArgumentException e) {
                e.printStackTrace();
                return null;
            }
        } else if (key != null) {
            try {
                T value = node.getValue(typeToken);
                set(key, (T) verify(verificationMap.get(key), value, node, modified));
                return value;
            } catch (ClassCastException | ObjectMappingException e) {
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
