package de.joshicodes.rja.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
