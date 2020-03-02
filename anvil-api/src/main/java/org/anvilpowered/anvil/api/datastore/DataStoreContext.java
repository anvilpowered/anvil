/*
 *   Anvil - AnvilPowered
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

package org.anvilpowered.anvil.api.datastore;

import org.anvilpowered.anvil.api.data.registry.Registry;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

// TODO: extract to interface
public abstract class DataStoreContext<TKey, TDataStore> {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private final List<ConnectionOpenedListener<TDataStore>> connectionOpenedListeners;
    private final List<ConnectionClosedListener<TDataStore>> connectionClosedListeners;
    protected final Registry registry;

    private TDataStore dataStore;
    private Class<?>[] entityClasses;
    private Class<TKey> tKeyClass;

    protected DataStoreContext(Registry registry) {
        connectionOpenedListeners = new ArrayList<>();
        connectionClosedListeners = new ArrayList<>();

        this.registry = registry;
        registry.addRegistryLoadedListener(this::registryLoaded);
    }

    protected void registryLoaded() {
        requestCloseConnection();
        dataStore = null;
    }

    protected abstract TDataStore loadDataStore();

    public TDataStore getDataStore() {
        if (dataStore == null) {
            dataStore = loadDataStore();
            notifyConnectionOpenedListeners(dataStore);
        }
        return Objects.requireNonNull(dataStore, "An error occurred while loading datastore");
    }

    @SafeVarargs
    protected final Class<?>[] calculateEntityClasses(final String baseScanPackage, final Class<? extends Annotation>... entityAnnotations) {
        if (entityAnnotations.length == 0) return EMPTY_CLASS_ARRAY;
        Reflections reflections = new Reflections(baseScanPackage, new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(entityAnnotations[0]);
        for (int i = 1; i < entityAnnotations.length; i++) {
            types.addAll(reflections.getTypesAnnotatedWith(entityAnnotations[i]));
        }
        return entityClasses = types.toArray(EMPTY_CLASS_ARRAY);
    }

    public final Class<?>[] getEntityClasses() {
        return entityClasses;
    }

    /**
     * @return First entityClass that contains {@code name}
     */
    public final Optional<Class<?>> getEntityClass(final String name) {
        Class<?> clazz = null;
        try {
            clazz = getEntityClassUnsafe(name);
        } catch (RuntimeException ignored) {
        }
        return Optional.ofNullable(clazz);
    }

    /**
     * @return First entityClass that contains (ignored case) the provided name
     */
    public final Class<?> getEntityClassUnsafe(final String name) {
        getDataStore(); // ensure that entityClasses is not null
        final String n = name.toLowerCase(Locale.ENGLISH);
        for (Class<?> entityClass : entityClasses) {
            if (entityClass.getSimpleName().toLowerCase(Locale.ENGLISH).contains(n)) {
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

    protected abstract void closeConnection(TDataStore dataStore);

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
