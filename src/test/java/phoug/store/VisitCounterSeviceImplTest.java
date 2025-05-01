package phoug.store.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import phoug.store.service.VisitCounterService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VisitCounterServiceImplTest {

    private VisitCounterService visitCounterService;

    @BeforeEach
    void setUp() {
        visitCounterService = new VisitCounterServiceImpl();
    }

    @Test
    void testRecordVisit_FirstTime() {
        String path = "/home";
        visitCounterService.recordVisit(path);
        assertEquals(1, visitCounterService.getVisitCount(path));
    }

    @Test
    void testRecordVisit_MultipleTimes() {
        String path = "/products/123";
        visitCounterService.recordVisit(path);
        visitCounterService.recordVisit(path);
        visitCounterService.recordVisit(path);
        assertEquals(3, visitCounterService.getVisitCount(path));
    }

    @Test
    void testGetVisitCount_NonExistingPath() {
        String path = "/nonexistent";
        assertEquals(0, visitCounterService.getVisitCount(path));
    }

    @Test
    void testGetAllVisits_Empty() {
        Map<String, Integer> visits = visitCounterService.getAllVisits();
        assertNotNull(visits);
        assertTrue(visits.isEmpty());
    }

    @Test
    void testGetAllVisits_WithData() {
        visitCounterService.recordVisit("/home");
        visitCounterService.recordVisit("/products");
        visitCounterService.recordVisit("/home");

        Map<String, Integer> visits = visitCounterService.getAllVisits();

        assertEquals(2, visits.get("/home"));
        assertEquals(1, visits.get("/products"));
        assertEquals(2, visits.size());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        String path = "/thread-safe";
        int threads = 10;
        int iterations = 1_000;

        Thread[] threadArray = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            threadArray[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    visitCounterService.recordVisit(path);
                }
            });
        }

        for (Thread thread : threadArray) thread.start();
        for (Thread thread : threadArray) thread.join();

        assertEquals(threads * iterations, visitCounterService.getVisitCount(path));
    }
}
