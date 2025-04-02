package phoug.store.utils;

import java.util.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryCache.class);

    private static class CacheEntry<V> {
        private final V value;
        private final long expiryTime;

        public CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public V getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiryTime;
        }
    }

    private final Map<K, CacheEntry<V>> cache;
    private final Queue<K> accessOrder; // Очередь для хранения порядка использования
    private final long ttlMillis;
    private final int maxSize;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public InMemoryCache() {
        this(600_000, 25); // 10 минут, максимум 25 элементов
    }

    public InMemoryCache(long ttlMillis, int maxSize) {
        this.ttlMillis = ttlMillis;
        this.maxSize = maxSize;
        this.cache = new ConcurrentHashMap<>();
        this.accessOrder = new ConcurrentLinkedQueue<>();

        scheduler.scheduleAtFixedRate(
                this::evictExpiredEntries, ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            logger.info("Cache miss for key: {}", key);
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            accessOrder.remove(key);
            logger.info("Cache entry expired for key: {}", key);
            return null;
        }
        logger.info("Cache hit for key: {}", key);
        return entry.getValue();
    }

    public void put(K key, V value) {
        if (cache.size() >= maxSize) {
            evictOldest(); // Удаляем самый старый элемент
        }
        CacheEntry<V> entry = new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis);
        cache.put(key, entry);
        accessOrder.offer(key);
        logger.info("Cache put for key: {}", key);
    }

    public void evict(K key) {
        cache.remove(key);
        accessOrder.remove(key);
        logger.info("Cache evict for key: {}", key);
    }

    public void clear() {
        cache.clear();
        accessOrder.clear();
        logger.info("Cache cleared");
    }

    private void evictExpiredEntries() {
        for (K key : new HashSet<>(cache.keySet())) {
            if (cache.get(key).isExpired()) {
                cache.remove(key);
                accessOrder.remove(key);
                logger.info("Cache entry evicted for key: {}", key);
            }
        }
    }

    private void evictOldest() {
        K oldestKey = accessOrder.poll(); // Удаляем самый старый
        if (oldestKey != null) {
            cache.remove(oldestKey);
            logger.info("Cache size exceeded, evicted oldest entry: {}", oldestKey);
        }
    }

    public Collection<V> getAllValues() {
        return cache.values().stream()
                .filter(entry -> !entry.isExpired())
                .map(CacheEntry::getValue)
                .toList();
    }
}
