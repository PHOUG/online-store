package phoug.store.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import phoug.store.model.LogTask;

public interface LogService {

    LogTask getTaskStatus(String taskId); // Изменено с Map<String, Object> на LogTask

    ResponseEntity<Resource> downloadLogFile(String taskId);

    String viewLogsByDate(String date);

    String createLogTask(String date);

}
