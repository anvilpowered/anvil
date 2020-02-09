package org.anvilpowered.anvil.common.util;

import org.anvilpowered.anvil.api.event.Event;
import org.anvilpowered.anvil.api.event.EventBus;
import org.anvilpowered.anvil.api.event.EventListener;
import org.anvilpowered.anvil.api.plugin.Implementation;
import org.anvilpowered.anvil.api.plugin.PlatformImplementation;
import org.anvilpowered.anvil.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

public class PluginManager {

    private static ArrayList<Plugin<?>> plugins = new ArrayList<>();
    private static HashMap<Plugin<?>, HashMap<String, Class<PlatformImplementation>>> implementations = new HashMap<>();
    private static EventBus eventBus = new EventBus();

    public static void addAndRegisterPlugin(Plugin<?> plugin) {
        plugins.add(plugin);
        //plugin.on
    }

    public static void registerListener(EventListener listener) {
        eventBus.registerListener(listener);
    }

    public static void registerImplementation(Plugin<?> plugin, Class<PlatformImplementation> impl) {
        if(!implementations.containsKey(plugin)) implementations.put(plugin, new HashMap<>());
        implementations.get(plugin).put(impl.getClass().getAnnotation(Implementation.class).implementation(), impl);
    }

    public static boolean doesImplementationExist(Plugin<?> plugin, String impl) {
        if(!implementations.containsKey(plugin)) return false;
        return implementations.get(plugin).containsKey(impl);
    }

    public static Class<PlatformImplementation> getImplementation(Plugin<?> plugin, String platform) {
        return implementations.get(plugin).get(platform);
    }

    public static void handleEvent(Event event) {
        eventBus.handleEvent(event);
    }

}
