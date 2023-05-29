package de.joshicodes.rja.cache;

import de.joshicodes.rja.util.MultiObject;
import de.joshicodes.rja.util.TrippleMap;

import java.util.HashMap;

public class CacheMap<K, T> {

    private final TrippleMap<K, T, Long> map;

    public CacheMap() {
        map = new TrippleMap<>();
    }

    public void put(K key, T t) {
        map.put(key, t, Cache.DEFAULT_LIFESPAN);
    }

    public void put(K key, T t, long lifespan) {
        map.put(key, t, System.currentTimeMillis() + lifespan);
    }

    public T get(K key) {
        MultiObject<T, Long> multiObject = map.get(key);
        if(multiObject == null) return null;
        if(multiObject.getSecond() < System.currentTimeMillis()) {
            map.remove(key);
            return null;
        }
        return multiObject.getFirst();
    }

    public void remove(K key) {
        map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(T t) {
        return getSimpleMap().containsValue(t);
    }

    public boolean containsFirst(K key, T t) {
        return map.containsFirst(key, t);
    }

    public boolean containsSecond(K key, Long lifespan) {
        return map.containsSecond(key, lifespan);
    }

    public void clear() {
        map.clear();
    }

    public HashMap<K, T> getSimpleMap() {
        HashMap<K, T> simpleMap = new HashMap<>();
        map.getMap().forEach((k, v) -> simpleMap.put(k, v.getFirst()));
        return simpleMap;
    }

    public TrippleMap<K, T, Long> getMap() {
        return map;
    }

}
