/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.api.datastore;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

// TODO: extract to interface
public abstract class DataStoreContext<TKey, TDataStore> {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private final List<Consumer<TDataStore>> connectionOpenedListeners;
    private final List<Consumer<TDataStore>> connectionClosedListeners;
    protected final Registry registry;

    @Nullable
    private TDataStore dataStore;
    private Class<?>[] entityClasses;
    private Class<TKey> tKeyClass;

    @Inject(optional = true)
    private ClassLoader classLoader;

    protected DataStoreContext(Registry registry) {
        connectionOpenedListeners = new ArrayList<>();
        connectionClosedListeners = new ArrayList<>();

        this.registry = registry;
        registry.whenLoaded(this::registryLoaded).register();
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
        return Preconditions.checkNotNull(dataStore, "An error occurred while loading datastore");
    }

    @SafeVarargs
    protected final Class<?>[] calculateEntityClasses(
        final String baseScanPackage, final Class<? extends Annotation>... entityAnnotations) {
        if (entityAnnotations.length == 0) return EMPTY_CLASS_ARRAY;
        Reflections reflections = new Reflections(
            baseScanPackage, new TypeAnnotationsScanner(), new SubTypesScanner(), classLoader);
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
     * @param name The name that the entity class contains
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
     * @param name The name that the entity class contains
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
        connectionOpenedListeners.forEach(listener -> listener.accept(dataStore));
    }

    public final void addConnectionOpenedListener(Consumer<TDataStore> connectionOpenedListener) {
        connectionOpenedListeners.add(connectionOpenedListener);
    }

    public final void removeConnectionOpenedListener(Consumer<TDataStore> connectionOpenedListener) {
        connectionOpenedListeners.remove(connectionOpenedListener);
    }

    private void notifyConnectionClosedListeners(TDataStore dataStore) {
        connectionClosedListeners.forEach(listener -> listener.accept(dataStore));
    }

    public final void addConnectionClosedListener(Consumer<TDataStore> connectionClosedListener) {
        connectionClosedListeners.add(connectionClosedListener);
    }

    public final void removeConnectionClosedListener(Consumer<TDataStore> connectionClosedListener) {
        connectionClosedListeners.remove(connectionClosedListener);
    }
}
