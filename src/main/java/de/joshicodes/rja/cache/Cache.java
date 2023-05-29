package de.joshicodes.rja.cache;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Cache<T> {

    public static final long DEFAULT_LIFESPAN = TimeUnit.HOURS.toMillis(1);

    private final HashMap<T, Long> list;

    private Timer timer;

    public Cache() {
        list = new HashMap<>();
    }

    /**
     * Schedules a task to clear expired entries
     * @param delay Delay between each run
     * @param unit Unit of the delay
     */
    public void scheduleClearExpired(long delay, TimeUnit unit) {
        if(timer != null) timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        clearExpired();
                    }
                },
                unit.toMillis(delay),
                unit.toMillis(delay)
        );
    }

    /**
     * Cancels the task to clear expired entries
     * If no task is scheduled, nothing happens
     * @see #scheduleClearExpired(long, TimeUnit)
     */
    public void cancelClearExpired() {
        if(timer != null) timer.cancel();
    }

    /**
     * Clears all expired entries one time
     */
    public void clearExpired() {
        list.keySet().stream().filter(t -> list.get(t) < System.currentTimeMillis()).forEach(list::remove);
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
                list.remove(t);
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
     * @return
     */
    public Stream<T> stream() {
        return list.keySet().stream();
    }

    /**
     * Returns the first object in the cache that matches the predicate
     * @param predicate Predicate to match
     * @return Object if it is in the cache and not expired, null otherwise
     */
    public T getIf(Predicate<T> predicate) {
        return list.keySet().stream().filter(predicate).findFirst().orElse(null);
    }

    public boolean contains(T t) {
        if(list.containsKey(t)) {
            if(list.get(t) >= System.currentTimeMillis()) {
                return true;
            } else list.remove(t); // Remove if expired
        }
        return false;
    }

    public boolean containsIf(Predicate<T> predicate) {
        return list.keySet().stream().anyMatch(predicate);
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

}
