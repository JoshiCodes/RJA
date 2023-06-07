package de.joshicodes.rja.object.enums;

/**
 * Defines which objects should be cached.
 * @see de.joshicodes.rja.RJABuilder#enableCaching(CachingPolicy...)
 * @see de.joshicodes.rja.RJABuilder#disableCaching(CachingPolicy...)
 */
public enum CachingPolicy {
    /**
     * Caches the message objects.
     */
    MESSAGE,

    /**
     * Caches the user and member objects.
     */
    MEMBER,

    /**
     * Caches the server objects.
     * This will also enable caching of the channel and category objects.
     */
    SERVER,

    /**
     * Caches the emote objects.
     * Not implemented yet, will have no effect.
     */
    EMOJI,

}
