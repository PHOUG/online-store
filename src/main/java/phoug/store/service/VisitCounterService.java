package phoug.store.service;

import java.util.Map;

public interface VisitCounterService {

    void recordVisit(String path);

    int getVisitCount(String path);

    Map<String, Integer> getAllVisits();
}
