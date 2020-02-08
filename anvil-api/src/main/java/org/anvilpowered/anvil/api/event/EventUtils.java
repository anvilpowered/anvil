package org.anvilpowered.anvil.api.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventUtils {

    public static ArrayList<Method> getMethodsWithAnnotation(Class<?> c, Class<? extends Annotation> a) {
        Method[] methods = c.getMethods();
        ArrayList<Method> out = new ArrayList<>();

        for(Method m : methods) {
            if(m.isAnnotationPresent(a)) out.add(m);
        }
        return out;
    }

}
