package org.anvilpowered.anvil.api.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) //So we can read the annotation values at runtime
public @interface EventHandler {

    EventPriority priority() default EventPriority.NORMAL;

}
