package de.joshicodes.rja.event;

import com.google.gson.JsonObject;
import de.joshicodes.rja.RJA;

/**
 * Represents an Event received from the Revolt API.
 * The RJA will automatically listen and handle to these events.
 * To listen to Events use the {@link EventListener} class.
 */
public abstract class IncomingEvent extends Event {

    private final String type;

    /**
     * Creates a new IncomingEvent instance.
     * @param rja The RJA instance
     * @param type The type of the event. Case-sensitive.
     */
    public IncomingEvent(RJA rja, String type) {
        super(rja);
        this.type = type;
    }

    /**
     * Handles the incoming event.
     * This method will be called by the RJA.
     * Your Event implementation should handle the event here and return a new instance of the event.
     *
     * @param object The object received from the websocket. This is the raw data.
     * @return The new instance of the event
     */
    public abstract IncomingEvent handle(RJA rja, JsonObject object);

    public String getType() {
        return type;
    }

}
