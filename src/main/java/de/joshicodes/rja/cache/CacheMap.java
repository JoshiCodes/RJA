package de.joshicodes.rja.cache;

import de.joshicodes.rja.util.Pair;
import de.joshicodes.rja.util.TrippleMap;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.function.Predicate;

public class CacheMap<K, T> {

    private final int maxSize;
    private final TrippleMap<K, T, Long> map;

    private long lastClear = System.currentTimeMillis();

    public CacheMap() {
        this(Cache.DEFAULT_MAX_SIZE);
    }

    public CacheMap(final int maxSize) {
        this.maxSize = maxSize;
        map = new TrippleMap<>();
    }

    /**
     * Clears all expired entries one time
     */
    public void clearExpired() {
        try {
            if((System.currentTimeMillis() - lastClear) < Cache.DEFAULT_CLEAR_INTERVAL) return;
            map.keySet().stream().filter(t -> map.get(t).getSecond() < System.currentTimeMillis()).forEach(map::remove);
            lastClear = System.currentTimeMillis();
        } catch (Exception ignored) { }  // TODO: Identify why this exception is thrown (ConcurrentModificationException)
    }


    public void put(K key, T t) {
        put(key, t, Cache.DEFAULT_LIFESPAN);
    }

    public void put(K key, T t, long lifespan) {
        if(map.size() >= maxSize) {
            // Clear expired entries
            clearExpired();
            // If the cache is still full, remove the first entry
            if(map.size() >= maxSize) {
                map.remove(map.keySet().iterator().next());
            }
        }
        map.put(key, t, System.currentTimeMillis() + lifespan);
    }

    public T get(K key) {
        Pair<T, Long> pair = map.get(key);
        if(pair == null) return null;
        if(pair.getSecond() < System.currentTimeMillis()) {
            clearExpired();
            return null;
        }
        return pair.getFirst();
    }

    public void remove(K key) {
        map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(T t) {
        clearExpired();
        return getSimpleMap().containsValue(t);
    }

    public boolean containsFirst(K key, T t) {
        clearExpired();
        return map.containsFirst(key, t);
    }

    public boolean containsSecond(K key, Long lifespan) {
        clearExpired();
        return map.containsSecond(key, lifespan);
    }

    public boolean containsIf(Predicate<K> predicate) {
        clearExpired();
        return map.getMap().keySet().stream().anyMatch(predicate);
    }

    public T getIf(Predicate<K> predicate) {
        clearExpired();
        return map.getMap().entrySet().stream().filter(entry -> predicate.test(entry.getKey())).findFirst().map(entry -> entry.getValue().getFirst()).orElse(null);
    }

    public void clear() {
        map.clear();
    }

    public HashMap<K, T> getSimpleMap() {
        HashMap<K, T> simpleMap = new HashMap<>();
        map.getMap().forEach((k, v) -> simpleMap.put(k, v.getFirst()));
        return simpleMap;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public TrippleMap<K, T, Long> getMap() {
        return map;
    }

    public int size() {
        return map.size();
    }

}
