package de.joshicodes.rja.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheTest {

    @Test
    public void testCache() throws InterruptedException {
        Cache<String> cache = new Cache<>();

        cache.add("test");

        assertTrue(cache.contains("test"));

        cache.add("test2", 1000);                        // put Object with lifetime of 1 second
        assertTrue(cache.contains("test2"));                           // Make sure it's in cache
        Thread.sleep(1005);                                   // Wait 1,005 seconds
        assertFalse(cache.contains("test2"));                          // Make sure lifetime works
        assertFalse(cache.stream().anyMatch(s -> s.equals("test2")));  // Make sure not in stream/list

        assertTrue(cache.contains("test"));                           // Make sure it's still in cache
        assertTrue(cache.stream().anyMatch(s -> s.equals("test")));   // Make sure it's still in stream/list

        cache.remove("test");                                     // Remove Object

        assertFalse(cache.contains("test"));                         // Make sure it's not in cache
        assertFalse(cache.stream().anyMatch(s -> s.equals("test"))); // Make sure it's not in stream/list

    }

}
