package phoug.store.utils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Реализация кэша в памяти с автоматическим удалением устаревших записей.
// Использует ConcurrentHashMap для потокобезопасности и планировщик задач
// для периодического удаления устаревших записей.
@SuppressWarnings("squid:S6829")
@Component
public class InMemoryCache<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryCache.class);

    // Внутренний класс, представляющий запись в кэше.
    // Содержит данные и время истечения.
    private static class CacheEntry<V> {
        private final V value;         // Кэшированное значение
        private final long expiryTime; // Время истечения срока действия записи

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

    // Карта для хранения кэшированных данных
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis; // Время жизни (TTL) в миллисекундах
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    // Конструктор по умолчанию
    public InMemoryCache() {
        this(600_000); // 5 минут (600 000 мс)
    }

    // Конструктор с указанием TTL.
    // @param ttlMillis Время жизни записей в миллисекундах
    public InMemoryCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
        // Запускаем процесс очистки кэша по расписанию
        scheduler.scheduleAtFixedRate(
                this::evictExpiredEntries, ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
    }


    // Получает данные из кэша по ключу.
    // @param key Ключ поиска
    // @return Значение из кэша или null, если запись отсутствует или устарела
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            logger.info("Cache miss for key: {}", key);
            return null;
        }
        if (entry.isExpired()) {
            logger.info("Cache entry expired for key: {}", key);
            cache.remove(key);
            return null;
        }
        logger.info("Cache hit for key: {}", key);
        return entry.getValue();
    }

    // Добавляет новую запись в кэш.
    // @param key Ключ для хранения
    // @param value Значение, которое нужно сохранить
    public void put(K key, V value) {
        CacheEntry<V> entry = new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis);
        cache.put(key, entry);
        logger.info("Cache put for key: {}", key);
    }

    // Удаляет запись из кэша по ключу.
    // @param key Ключ для удаления
    public void evict(K key) {
        cache.remove(key);
        logger.info("Cache evict for key: {}", key);
    }

    // Полностью очищает кэш.
    public void clear() {
        cache.clear();
        logger.info("Cache cleared");
    }

    // Удаляет устаревшие записи из кэша.
    private void evictExpiredEntries() {
        for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
            if (entry.getValue().isExpired()) {
                cache.remove(entry.getKey());
                logger.info("Cache entry evicted for key: {}", entry.getKey());
            }
        }
    }


    public Collection<V> getAllValues() {
        return cache.values().stream()
                .filter(entry -> !entry.isExpired())
                .map(CacheEntry::getValue)
                .toList();
    }
}
