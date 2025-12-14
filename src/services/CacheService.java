package services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 * A thread-safe caching system with LRU eviction and statistics.
 *
 * @param <K> The type of keys
 * @param <V> The type of values
 */
public class CacheService<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final int maxSize;
    private final AtomicInteger hits = new AtomicInteger(0);
    private final AtomicInteger misses = new AtomicInteger(0);
    private final AtomicInteger evictions = new AtomicInteger(0);
    private final AtomicLong totalHitTime = new AtomicLong(0); // in nanoseconds
    private final AtomicLong totalMissTime = new AtomicLong(0); // in nanoseconds

    public CacheService(int maxSize) {
        this.cache = new ConcurrentHashMap<>();
        this.maxSize = maxSize;
    }

    /**
     * Retrieves a value from the cache.
     * Updates hit/miss stats and access timestamp.
     *
     * @param key The key to look up
     * @return The value if found, null otherwise
     */
    public V get(K key) {
        long start = System.nanoTime();
        CacheEntry<V> entry = cache.get(key);
        long end = System.nanoTime();

        if (entry != null) {
            hits.incrementAndGet();
            totalHitTime.addAndGet(end - start);
            entry.updateLastAccessed();
            return entry.getValue();
        } else {
            misses.incrementAndGet();
            totalMissTime.addAndGet(end - start);
            return null;
        }
    }

    /**
     * Puts a value into the cache.
     * Evicts the least recently used item if max size is reached.
     *
     * @param key   The key
     * @param value The value
     */
    public synchronized void put(K key, V value) {
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            evictLRU();
        }
        cache.put(key, new CacheEntry<>(value));
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
        hits.set(0);
        misses.set(0);
        evictions.set(0);
        totalHitTime.set(0);
        totalMissTime.set(0);
    }

    private void evictLRU() {
        K lruKey = null;
        long oldestTime = Long.MAX_VALUE;

        // O(N) scan is acceptable for N=150 and simpler for thread safety than a
        // concurrent queue
        for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
            if (entry.getValue().getLastAccessed() < oldestTime) {
                oldestTime = entry.getValue().getLastAccessed();
                lruKey = entry.getKey();
            }
        }

        if (lruKey != null) {
            cache.remove(lruKey);
            evictions.incrementAndGet();
        }
    }

    public String getStats() {
        int totalRequests = hits.get() + misses.get();
        double hitRate = totalRequests == 0 ? 0 : (double) hits.get() / totalRequests * 100;
        double missRate = totalRequests == 0 ? 0 : (double) misses.get() / totalRequests * 100;

        double avgHitTime = hits.get() == 0 ? 0 : (double) totalHitTime.get() / hits.get() / 1000000.0; // ms
        double avgMissTime = misses.get() == 0 ? 0 : (double) totalMissTime.get() / misses.get() / 1000000.0; // ms

        StringBuilder stats = new StringBuilder();
        stats.append("\n=== Cache Statistics ===\n");
        stats.append(String.format("Entries: %d / %d\n", cache.size(), maxSize));
        stats.append(String.format("Hits: %d (%.1f%%)\n", hits.get(), hitRate));
        stats.append(String.format("Misses: %d (%.1f%%)\n", misses.get(), missRate));
        stats.append(String.format("Evictions: %d\n", evictions.get()));
        stats.append(String.format("Avg Hit Time: %.3f ms\n", avgHitTime));
        stats.append(String.format("Avg Miss Time: %.3f ms\n", avgMissTime));

        // Approximate memory usage (Value size is unknown, just counting entries
        // overhead)
        // stats.append("Memory Usage: ~" + (cache.size() * 128) + " bytes
        // (Estimate)\n");

        return stats.toString();
    }

    public Map<K, CacheEntry<V>> getCacheMap() {
        return Collections.unmodifiableMap(cache);
    }

    public static class CacheEntry<V> {
        private final V value;
        private volatile long lastAccessed;
        private final long lastUpdated;

        public CacheEntry(V value) {
            this.value = value;
            this.lastAccessed = System.currentTimeMillis();
            this.lastUpdated = System.currentTimeMillis();
        }

        public V getValue() {
            return value;
        }

        public long getLastAccessed() {
            return lastAccessed;
        }

        public long getLastUpdated() {
            return lastUpdated;
        }

        public void updateLastAccessed() {
            this.lastAccessed = System.currentTimeMillis();
        }
    }
}
