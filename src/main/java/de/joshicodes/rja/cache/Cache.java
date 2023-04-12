package de.joshicodes.rja.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @param <T>
 * @deprecated At this point in time, RJA is not able to receive single objects from the server. Objects are sent once at startup and at server updates, etc.
 *             This result in having to store them permanently in the cache. This class is used to store objects temporarily and cannot be used for permanent storage.
 *             I'm hoping to get a function to receive single user oder channel objects to allow temporary storage.
 */
public class Cache<T> {

    public static final long DEFAULT_LIFESPAN = TimeUnit.HOURS.toMillis(1);

    private final HashMap<T, Long> list;

    public Cache() {
        list = new HashMap<>();
    }

    public void add(T t, long lifespan) {
        list.put(t, System.currentTimeMillis() + lifespan);
    }

    public void add(T t) {
        add(t, DEFAULT_LIFESPAN);
    }

    public void remove(T t) {
        list.remove(t);
    }

    public T get(T t) {
        if(list.containsKey(t)) {
            if(list.get(t) >= System.currentTimeMillis()) {
                return t;
            } else {
                list.remove(t);
            }
        }
        return null;
    }

    public void clear() {
        list.clear();
    }

    public Stream<T> stream() {
        return list.keySet().stream();
    }

    public T getIf(Predicate<T> predicate) {
        return list.keySet().stream().filter(predicate).findFirst().orElse(null);
    }

}
