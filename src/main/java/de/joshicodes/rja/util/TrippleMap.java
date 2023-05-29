package de.joshicodes.rja.util;

import java.util.HashMap;

/**
 * If you just need a simple Map use {@link java.util.HashMap} or {@link java.util.TreeMap}.
 * @param <K> The key
 * @param <B> The first value
 * @param <C> The second value
 */
public class TrippleMap<K, B, C> {

    private final HashMap<K, MultiObject<B, C>> map;

    public TrippleMap() {
        map = new HashMap<>();
    }

    public void put(K key, B b, C c) {
        map.put(key, new MultiObject<>(b, c));
    }

    public void put(K key, MultiObject<B, C> multiObject) {
        map.put(key, multiObject);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsFirst(K key, B b) {
        MultiObject<B, C> multiObject = map.get(key);
        if(multiObject == null) return false;
        return multiObject.getFirst().equals(b);
    }

    public boolean containsSecond(K key, C c) {
        MultiObject<B, C> multiObject = map.get(key);
        if(multiObject == null) return false;
        return multiObject.getSecond().equals(c);
    }

    public B getFirst(K key) {
        MultiObject<B, C> multiObject = map.get(key);
        if(multiObject == null) return null;
        return multiObject.getFirst();
    }

    public C getSecond(K key) {
        MultiObject<B, C> multiObject = map.get(key);
        if(multiObject == null) return null;
        return multiObject.getSecond();
    }

    public void clear() {
        map.clear();
    }

    public MultiObject<B, C> get(K key) {
        return map.get(key);
    }

    public HashMap<K, MultiObject<B, C>> getMap() {
        return map;
    }

}
