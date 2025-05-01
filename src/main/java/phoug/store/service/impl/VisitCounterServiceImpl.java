package phoug.store.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import phoug.store.service.VisitCounterService;

@Service
public class VisitCounterServiceImpl implements VisitCounterService {

    private final ConcurrentHashMap<String, AtomicInteger> visitCounts = new ConcurrentHashMap<>();

    @Override
    public void recordVisit(String path) {
        visitCounts.computeIfAbsent(path, key -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public int getVisitCount(String path) {
        return visitCounts.getOrDefault(path, new AtomicInteger(0)).get();
    }

    @Override
    public Map<String, Integer> getAllVisits() {
        Map<String, Integer> snapshot = new ConcurrentHashMap<>();
        for (Map.Entry<String, AtomicInteger> entry : visitCounts.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().get());
        }
        return snapshot;
    }
}
