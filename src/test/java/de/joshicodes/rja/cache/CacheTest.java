package de.joshicodes.rja.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheTest {

    @Test
    public void testCache() {
        Cache<String> cache = new Cache<>(Cache.DEFAULT_MAX_SIZE);
        for (int i = 0; i < Cache.DEFAULT_MAX_SIZE * 2; i++) {
            cache.add("key" + i);
        }
        assertEquals(Cache.DEFAULT_MAX_SIZE, cache.size());
    }

}
