package de.joshicodes.rja.cache;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Cache<T> {

    public static final long DEFAULT_LIFESPAN = TimeUnit.HOURS.toMillis(1);
    public static final int DEFAULT_MAX_SIZE = 500;
    public static final long DEFAULT_CLEAR_INTERVAL = TimeUnit.MINUTES.toMillis(1); // 1 Minute

    private final int maxSize;
    private final HashMap<T, Long> list;
    private long lastClear = System.currentTimeMillis();

    public Cache() {
        this(DEFAULT_MAX_SIZE);
    }

    public Cache(final int maxSize) {
        this.maxSize = maxSize;
        list = new HashMap<>();
    }

    /**
     * Clears all expired entries one time
     */
    public void clearExpired() {
        try {
            if (size() >= getMaxSize() || (System.currentTimeMillis() - lastClear) < DEFAULT_CLEAR_INTERVAL) return;
            list.keySet().stream().filter(t -> list.get(t) < System.currentTimeMillis()).forEach(list::remove);
            lastClear = System.currentTimeMillis();
        } catch (Exception ignored) { } // TODO: Identify why this exception is thrown (ConcurrentModificationException)
    }

    public void add(T t, long lifespan) {
        if(list.size() >= maxSize) {
            clearExpired();  // Clear expired entries
            if(list.size() >= maxSize) {
                // If the cache is still full, remove the first entry
                list.remove(list.keySet().stream().findFirst().orElse(null));
            }
        }
        list.put(t, System.currentTimeMillis() + lifespan);
    }

    public void add(T t) {
        add(t, DEFAULT_LIFESPAN);
    }

    public void remove(T t) {
        list.remove(t);
    }

    /**
     * Returns the object if it is in the cache and not expired
     * If the object is expired, it will be removed from the cache and null will be returned
     * @param t Object to get
     * @return Object if it is in the cache and not expired, null otherwise
     */
    public T get(T t) {
        if(list.containsKey(t)) {
            if(list.get(t) >= System.currentTimeMillis()) {
                return t;
            } else {
                clearExpired(); // Clear expired entries
            }
        }
        return null;
    }

    /**
     * Clears the cache
     */
    public void clear() {
        list.clear();
    }

    /**
     * Returns a stream of all objects in the cache
     * Also clears all expired entries
     * @return Stream of all objects in the cache
     */
    public Stream<T> stream() {
        clearExpired();
        return list.keySet().stream();
    }

    /**
     * Returns the first object in the cache that matches the predicate
     * @param predicate Predicate to match
     * @return Object if it is in the cache and not expired, null otherwise
     */
    public T getIf(Predicate<T> predicate) {
        clearExpired();
        return list.keySet().stream().filter(predicate).findFirst().orElse(null);
    }

    public boolean contains(T t) {
        if(list.containsKey(t)) {
            if(list.get(t) >= System.currentTimeMillis()) {
                return true;
            } else clearExpired();
        }
        return false;
    }

    public boolean containsIf(Predicate<T> predicate) {
        return list.keySet().stream().anyMatch(predicate);
    }

    /**
     * Returns the maximum size of the cache
     * @return Maximum size of the cache
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the HashMap of all objects and their expiration time
     * To just get the objects, use {@link #stream()}
     * To get one object, use {@link #get(Object)} or {@link #getIf(Predicate)}
     * @return HashMap of all objects and their expiration time
     */
    public HashMap<T, Long> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

}
