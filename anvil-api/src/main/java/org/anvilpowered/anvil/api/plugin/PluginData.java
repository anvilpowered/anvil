package org.anvilpowered.anvil.api.plugin;

public @interface PluginData {

    String name();
    String version() default "1.0.0";

}
