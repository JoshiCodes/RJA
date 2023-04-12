package de.joshicodes.rja.exception;

import de.joshicodes.rja.object.enums.CachingPolicy;

public class DisabledCacheException extends RuntimeException {

    public DisabledCacheException(CachingPolicy policy) {
        super("Tried to access a cached object, but the caching policy for " + policy.name() + " is disabled.");
    }

}
