package MatchingGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {

    /*
    Our objective is to map any "class" of events to a DS consisting of "consumers" of that event. Therefore it makes sense
    for the keys to take the form of wildcard, but how exactly do we get the DS List<Consumer<T>> to match the
    class of events in the key?

    Example:
    Class<UserCreateLobbyEvent>, List<Consumer<UserCreateLobbyEvent>>

    We could do Map<Class<? extends Event>, List<Consumer<? extends Event>>> but then we aren't enforcing
    EventA:Consume<EventA> which makes logical sense

    Potential solution:
    simply hardcode the hashmap
    put(UserCreateLobbyEvent.class, new ArrayList<Consumer<UserCreateLobbyEventHandler>>())
    that way we don't allow a mismatch to occur
     */
    public static Map<Class<? extends Event>, List<Consumer<? extends Event>>> handlers = new HashMap<>();

    public static <E extends Event> void registerHandler(Class<E> eventType, Consumer<E> handler) {
        //handler are
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    public static <E extends Event> void dispatch (E event) {
        /*
        Gaurenteed a list of consumers for that particular event, however because of our initial
        declaration combined with our adhoc "enforcement" , we can't assume the type of DS retrieved from the array
         */
        List<Consumer<? extends Event>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            //eventHandlers.forEach(accept(event));
            for (Consumer<? extends Event> handler : eventHandlers) {
                //Although at runtime we know what this type is, we will have to cast to compile
                //Interesting: ((Consumer)handler).accept(event) also works
                ((Consumer<E>)handler).accept(event);
            }
        }
    }
}
