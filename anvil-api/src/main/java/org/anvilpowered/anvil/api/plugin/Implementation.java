package org.anvilpowered.anvil.api.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value= RetentionPolicy.RUNTIME)
public @interface Implementation {

    String implementation();

}
