package de.joshicodes.rja.event;

/**
 * This class is used to receive events from the Revolt API.
 * To use this class, create a new child class and extend this class.
 * Then, register the child class using the {@link de.joshicodes.rja.RJABuilder#registerEventListener(EventListener)} method.
 * To Listen to an Event, create a method and append the {@link EventHandler} annotation to it.
 */
public interface EventListener {
}
