package de.joshicodes.rja.util;

import java.util.HashMap;
import java.util.Set;

/**
 * If you just need a simple Map use {@link java.util.HashMap} or {@link java.util.TreeMap}.
 * @param <K> The key
 * @param <B> The first value
 * @param <C> The second value
 */
public class TrippleMap<K, B, C> {

    private final HashMap<K, Pair<B, C>> map;

    public TrippleMap() {
        map = new HashMap<>();
    }

    public void put(K key, B b, C c) {
        map.put(key, new Pair<>(b, c));
    }

    public void put(K key, Pair<B, C> pair) {
        map.put(key, pair);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsFirst(K key, B b) {
        Pair<B, C> pair = map.get(key);
        if(pair == null) return false;
        return pair.getFirst().equals(b);
    }

    public boolean containsSecond(K key, C c) {
        Pair<B, C> pair = map.get(key);
        if(pair == null) return false;
        return pair.getSecond().equals(c);
    }

    public B getFirst(K key) {
        Pair<B, C> pair = map.get(key);
        if(pair == null) return null;
        return pair.getFirst();
    }

    public C getSecond(K key) {
        Pair<B, C> pair = map.get(key);
        if(pair == null) return null;
        return pair.getSecond();
    }

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Pair<B, C> get(K key) {
        return map.get(key);
    }

    public HashMap<K, Pair<B, C>> getMap() {
        return map;
    }

}
