package phoug.store.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import phoug.store.exception.LogReadException;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.exception.TaskNotFoundException;
import phoug.store.model.LogTask;
import phoug.store.service.LogService;

@Service
public class LogServiceImpl implements LogService {

    private static final String LOGS_DIRECTORY = "/Users/phoug/onlineStore/logs";
    private static final String MAIN_LOG_FILE = "/Users/phoug/onlineStore/logs/online-store.log";
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
            throw new TaskNotFoundException("Task not found");
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
        LocalDate requestedDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            requestedDate = LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd", e);
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = requestedDate.format(fileFormatter);

        Path logPath;
        if (requestedDate.equals(today)) {
            logPath = Paths.get("logs", "online-store.log");
        } else {
            logPath = Paths.get("logs", "online-store-" + formattedDate + ".log");
        }

        if (!Files.exists(logPath)) {
            Path gzLogPath = Paths.get("logs", "online-store.log." + formattedDate + ".0.gz");
            if (!Files.exists(gzLogPath)) {
                throw new ResourceNotFoundException(
                        "No logs found for the given date: " + formattedDate);
            }

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(
                    Files.newInputStream(gzLogPath));
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(gzipInputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new LogReadException(
                        "Error reading .gz log file for date: " + formattedDate, e);
            }
        }

        try (Stream<String> lines = Files.lines(logPath)) {
            return lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new LogReadException("Error reading log file for date: " + formattedDate, e);
        }
    }
}
