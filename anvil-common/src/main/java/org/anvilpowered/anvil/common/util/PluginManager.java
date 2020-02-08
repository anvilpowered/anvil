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

    private ArrayList<Plugin<?>> plugins = new ArrayList<>();
    private HashMap<Plugin<?>, HashMap<String, Class<PlatformImplementation>>> implementations = new HashMap<>();
    private EventBus eventBus = new EventBus();

    public void addAndRegisterPlugin(Plugin<?> plugin) {
        this.plugins.add(plugin);
        //plugin.on
    }

    public void registerListener(EventListener listener) {
        eventBus.registerListener(listener);
    }

    public void registerImplementation(Plugin<?> plugin, Class<PlatformImplementation> impl) {
        if(!this.implementations.containsKey(plugin)) this.implementations.put(plugin, new HashMap<>());
        this.implementations.get(plugin).put(impl.getClass().getAnnotation(Implementation.class).implementation(), impl);
    }

    public boolean doesImplementationExist(Plugin<?> plugin, String impl) {
        if(!this.implementations.containsKey(plugin)) return false;
        return this.implementations.get(plugin).containsKey(impl);
    }

    public Class<PlatformImplementation> getImplementation(Plugin<?> plugin, String platform) {
        return this.implementations.get(plugin).get(platform);
    }

    public void handleEvent(Event event) {
        eventBus.handleEvent(event);
    }

}
