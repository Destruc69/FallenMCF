package paul.fallen.stevebot.mod.events;

import net.minecraftforge.eventbus.api.Event;

public interface EventManager {


    /**
     * Adds the given {@link EventListener} to this manager.
     *
     * @param listener the listener
     */
    void addListener(EventListener<? extends Event> listener);

    /**
     * Removes the given {@link EventListener} from this manager.
     *
     * @param listener the listener
     */
    void removeListener(EventListener<? extends Event> listener);

    /**
     * Distributes the given event to all listening {@link EventListener}s.
     *
     * @param event the event.
     */
    void event(Event event);

}
