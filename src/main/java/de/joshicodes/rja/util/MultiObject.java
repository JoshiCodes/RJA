package de.joshicodes.rja.util;

/**
 * A Object that can hold two values
 * @param <B> The first value
 * @param <C> The second value
 */
public record MultiObject<B, C>(B first, C second) {

    public B getFirst() {
        return first;
    }

    public C getSecond() {
        return second;
    }

}
