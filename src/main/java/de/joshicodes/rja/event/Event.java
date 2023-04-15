package de.joshicodes.rja.event;

import de.joshicodes.rja.RJA;

import java.util.logging.Logger;

/**
 * Represents an Event that can be called by the RJA.
 * To listen to Events use the {@link EventListener} class.
 */
public abstract class Event {

    private final RJA rja;

    public Event(RJA rja) {
        this.rja = rja;
    }

    public RJA getRJA() {
        return rja;
    }

    public Logger getLogger() {
        return rja.getLogger();
    }

}
