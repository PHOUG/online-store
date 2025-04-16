package phoug.store.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import phoug.store.model.LogTask;
import phoug.store.service.LogService;

@Service
public class LogServiceImpl implements LogService {

    private static final String LOGS_DIRECTORY = "/Users/phoug/onlineStore/logs";
    private static final String MAIN_LOG_FILE = "/Users/phoug/onlineStore/logs/application.log";
    private final Map<String, LogTask> tasks = new ConcurrentHashMap<>();

    @Override
    public String generateLogFile(String date, String logType) {
        String taskId = UUID.randomUUID().toString();
        LogTask task = new LogTask(taskId, "PENDING", date, LocalDateTime.now());
        tasks.put(taskId, task);

        processLogGenerationAsync(taskId, date, logType);
        return taskId;
    }

    @Async
    public void processLogGenerationAsync(String taskId, String date, String logType) {
        LogTask task = tasks.get(taskId);
        task.setStatus("PROCESSING");

        try {
            Path logPath = Paths.get(MAIN_LOG_FILE);
            if (!Files.exists(logPath)) {
                throw new IOException("Main log file not found");
            }

            Path logsDir = Paths.get(LOGS_DIRECTORY);
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }

            String filteredLogs;
            try (Stream<String> lines = Files.lines(logPath)) {
                filteredLogs = lines
                        .filter(line -> line.contains(date))
                        .filter(line -> logType == null || line.contains(logType))
                        .collect(Collectors.joining("\n"));
            }

            if (filteredLogs.isEmpty()) {
                throw new IOException("No logs found for specified criteria");
            }

            String safeDate = date.replaceAll("[^a-zA-Z0-9-_]", "_");
            String filename = String.format("logs-%s-%s.log", safeDate, taskId);
            Path outputFile = logsDir.resolve(filename);

            if (!outputFile.startsWith(Paths.get(LOGS_DIRECTORY))) {
                throw new SecurityException("Invalid path: potential path traversal attempt.");
            }

            Files.write(outputFile, filteredLogs.getBytes());

            task.setStatus("COMPLETED");
            task.setFilePath(outputFile.toString());
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
        }
    }

    @Override
    public LogTask getTaskStatus(String taskId) {
        LogTask task = tasks.get(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        return task;
    }

    @Override
    public ResponseEntity<Resource> downloadLogFile(String taskId) {
        LogTask task = tasks.get(taskId);
        if (task == null || !"COMPLETED".equals(task.getStatus())) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(task.getFilePath()).normalize();

            if (!filePath.startsWith(Paths.get(LOGS_DIRECTORY))) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + filePath.getFileName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public String viewLogsByDate(String date) {
        try {
            Path logPath = Paths.get(MAIN_LOG_FILE);
            if (!Files.exists(logPath)) {
                throw new IOException("Main log file not found");
            }

            try (Stream<String> lines = Files.lines(logPath)) {
                return lines
                        .filter(line -> line.contains(date))
                        .collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error viewing logs", e);
        }
    }
}
