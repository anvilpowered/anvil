package org.anvilpowered.anvil.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;

public class EventBus {

    protected HashMap<EventPriority, HashMap< Class<? extends Event>, ArrayList<HandlerListenerPair>>> eventHandlers = new HashMap<>(); //Event priorities -> Event -> [Actual method handlers]

    public void addHandler(EventPriority priority, Class<? extends Event> event, HandlerListenerPair pair) {
        if(!eventHandlers.containsKey(priority)) eventHandlers.put(priority, new HashMap<>());
        if(!eventHandlers.get(priority).containsKey(event)) eventHandlers.get(priority).put(event,new ArrayList<>());
        eventHandlers.get(priority).get(event).add(pair);
    }

    public Event handleEvent(Event event) {
        for(EventPriority priority : EventPriority.values()) {
            if(!eventHandlers.containsKey(priority)) continue;
            HashMap< Class<? extends Event>, ArrayList<HandlerListenerPair>> handlers = eventHandlers.get(priority);

            Class implementedInterface = event.getClass().getInterfaces()[0];
            ArrayList<HandlerListenerPair> methods = handlers.get(implementedInterface);
            if(methods == null || methods.size() == 0) continue;

            for(HandlerListenerPair pair : methods) {
                try {
                    Parameter[] params = pair.handler.getParameters();
                    Class<?> target = params[0].getType().asSubclass(Event.class);
                    pair.handler.invoke(pair.listener, target.cast(event));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }

    public void registerListener(EventListener listener) {
        ArrayList<Method> methods = EventUtils.getMethodsWithAnnotation(listener.getClass(), EventHandler.class);
        for(Method method : methods) {
            Parameter[] params = method.getParameters();
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            Class<? extends Event> target = params[0].getType().asSubclass(Event.class);
            HandlerListenerPair pair = new HandlerListenerPair();
            pair.listener = listener;
            pair.handler = method;
            addHandler(annotation.priority(), target, pair);
        }
    }
}
