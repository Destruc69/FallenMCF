package paul.fallen.stevebot.mod.events;

import net.minecraftforge.eventbus.api.Event;

public interface EventListener<E extends Event> {


    /**
     * @return the class of the event this listener is listening to
     */
    Class<E> getEventClass();

    /**
     * @param event the event
     */
    void onEvent(E event);

}
