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

package rocks.milspecsg.msrepository.datastore;

import com.google.inject.Injector;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class DataStoreContext<TKey, TDataStore, TDataStoreConfig extends DataStoreConfig> {

    private final List<ConnectionOpenedListener<TDataStore>> connectionOpenedListeners;
    private final List<ConnectionClosedListener<TDataStore>> connectionClosedListeners;
    private final ConfigurationService configurationService;
    private final TDataStoreConfig config;


    private TDataStore dataStore = null;
    private Class<?>[] entityClasses;
    private Class<TKey> tKeyClass;


    public DataStoreContext(TDataStoreConfig config, Injector injector) {
        this.connectionOpenedListeners = new ArrayList<>();
        this.connectionClosedListeners = new ArrayList<>();

        ConfigurationService configurationService = injector.getInstance(config.getConfigurationServiceKey());
        configurationService.addConfigLoadedListener(this::configLoaded);
        this.configurationService = configurationService;
        this.config = config;
    }

    protected abstract void configLoaded(Object plugin);

    protected final void setDataStore(TDataStore dataStore) {
        requestCloseConnection();
        this.dataStore = dataStore;
        notifyConnectionOpenedListeners(dataStore);
    }

    public final Optional<TDataStore> getDataStore() {
        return Optional.ofNullable(dataStore);
    }

    @SafeVarargs
    protected final Class<?>[] calculateEntityClasses(final String baseScanPackage, final Class<? extends Annotation>... entityAnnotations) {
        if (entityAnnotations.length == 0) return new Class<?>[0];
        Reflections reflections = new Reflections(baseScanPackage, new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(entityAnnotations[0]);
        for (int i = 1; i < entityAnnotations.length; i++) {
            types.addAll(reflections.getTypesAnnotatedWith(entityAnnotations[i]));
        }
        return entityClasses = types.toArray(new Class<?>[0]);
    }

    public final Class<?>[] getEntityClasses() {
        return entityClasses;
    }

    /**
     * @return First entityClass that contains {@param name}
     */
    public final Optional<Class<?>> getEntityClass(final String name) {
        Class<?> clazz = null;
        try {
            clazz = getEntityClassUnsafe(name);
        } catch (Exception ignored) {
        }
        return Optional.ofNullable(clazz);
    }

    /**
     * @return First entityClass that contains {@param name}
     */
    public final Class<?> getEntityClassUnsafe(final String name) {
        final String n = name.toLowerCase();
        for (Class<?> entityClass : entityClasses) {
            if (entityClass.getSimpleName().toLowerCase().contains(n)) {
                return entityClass;
            }
        }
        throw new IllegalStateException("Could not find EntityClass for " + name);
    }

    protected final void setTKeyClass(Class<TKey> tKeyClass) {
        this.tKeyClass = tKeyClass;
    }

    public final Class<TKey> getTKeyClass() {
        return tKeyClass;
    }

    protected final ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public final TDataStoreConfig getConfig() {
        return config;
    }

    abstract protected void closeConnection(TDataStore dataStore);

    protected final void requestCloseConnection() {
        if (dataStore != null) {
            notifyConnectionClosedListeners(dataStore);
            closeConnection(dataStore);
            dataStore = null;
        }
    }

    private void notifyConnectionOpenedListeners(TDataStore dataStore) {
        connectionOpenedListeners.forEach(listener -> listener.loaded(dataStore));
    }

    public final void addConnectionOpenedListener(ConnectionOpenedListener<TDataStore> connectionOpenedListener) {
        connectionOpenedListeners.add(connectionOpenedListener);
    }

    public final void removeConnectionOpenedListener(ConnectionOpenedListener<TDataStore> connectionOpenedListener) {
        connectionOpenedListeners.remove(connectionOpenedListener);
    }

    private void notifyConnectionClosedListeners(TDataStore dataStore) {
        connectionClosedListeners.forEach(listener -> listener.closed(dataStore));
    }

    public final void addConnectionClosedListener(ConnectionClosedListener<TDataStore> connectionClosedListener) {
        connectionClosedListeners.add(connectionClosedListener);
    }

    public final void removeConnectionClosedListener(ConnectionClosedListener<TDataStore> connectionClosedListener) {
        connectionClosedListeners.remove(connectionClosedListener);
    }
}
