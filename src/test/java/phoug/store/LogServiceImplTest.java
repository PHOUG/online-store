package phoug.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import phoug.store.exception.LogReadException;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.exception.TaskNotFoundException;
import phoug.store.model.LogTask;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;
import phoug.store.service.impl.LogServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    private LogServiceImpl logService;

    private LogTask completedTask;
    private LogTask pendingTask;
    private String completedTaskId;
    private String pendingTaskId;

    @BeforeEach
    void setUp() throws Exception {
        Executor executor = Executors.newSingleThreadExecutor();
        logService = new LogServiceImpl(executor);

        completedTaskId = UUID.randomUUID().toString();
        pendingTaskId = UUID.randomUUID().toString();

        // Create default logs directory
        Path logsDir = Paths.get("logs");
        Files.createDirectories(logsDir);
        Path completedFile = logsDir.resolve("completed.log");
        Files.writeString(completedFile, "Completed log content");

        completedTask = new LogTask(completedTaskId, "COMPLETED", "2024-04-01", LocalDateTime.now());
        completedTask.setFilePath(completedFile.toString());

        pendingTask = new LogTask(pendingTaskId, "PENDING", "2024-04-01", LocalDateTime.now());

        // Inject tasks via reflection
        Field tasksField = LogServiceImpl.class.getDeclaredField("tasks");
        tasksField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, LogTask> tasks = (Map<String, LogTask>) tasksField.get(logService);
        tasks.put(completedTaskId, completedTask);
        tasks.put(pendingTaskId, pendingTask);
    }

    @Test
    void testGetTaskStatus_Valid() {
        LogTask result = logService.getTaskStatus(completedTaskId);
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void testGetTaskStatus_Invalid() {
        assertThrows(TaskNotFoundException.class, () -> logService.getTaskStatus("invalid-id"));
    }

    @Test
    void testDownloadLogFile_TaskNotFound() {
        ResponseEntity<Resource> response = logService.downloadLogFile("unknown-id");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDownloadLogFile_NotCompleted() {
        ResponseEntity<Resource> response = logService.downloadLogFile(pendingTaskId);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testDownloadLogFile_InvalidPath() {
        completedTask.setFilePath("/etc/passwd");
        ResponseEntity<Resource> response = logService.downloadLogFile(completedTaskId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDownloadLogFile_Success() throws Exception {
        // Read the real LOGS_DIRECTORY constant via reflection
        Field logsDirField = LogServiceImpl.class.getDeclaredField("LOGS_DIRECTORY");
        logsDirField.setAccessible(true);
        String logsDir = (String) logsDirField.get(null);

        // Create test file inside the real logs directory
        Path baseDir = Paths.get(logsDir);
        Files.createDirectories(baseDir);
        Path logFile = baseDir.resolve("test-file.log");
        Files.writeString(logFile, "Test content");

        completedTask.setFilePath(logFile.toString());
        ResponseEntity<Resource> response = logService.downloadLogFile(completedTaskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("attachment; filename=\"test-file.log\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    @Test
    void testDownloadLogFile_InternalServerError() {
        // Trigger exception by setting filePath to null
        completedTask.setFilePath(null);
        ResponseEntity<Resource> response = logService.downloadLogFile(completedTaskId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testViewLogsByDate_CurrentDate() throws IOException {
        Path todayLog = Paths.get("logs/online-store.log");
        Files.createDirectories(todayLog.getParent());
        Files.writeString(todayLog, "Today log content");

        String content = logService.viewLogsByDate(LocalDate.now().toString());
        assertTrue(content.contains("Today log content"));

        Files.deleteIfExists(todayLog);
    }

    @Test
    void testViewLogsByDate_ArchivedGzLog() throws IOException {
        String date = "2022-01-01";
        Path gzFile = Paths.get("logs/online-store.log." + date + ".0.gz");
        Files.createDirectories(gzFile.getParent());
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(Files.newOutputStream(gzFile));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(gzipOut))) {
            writer.write("GZ log content");
        }

        String content = logService.viewLogsByDate(date);
        assertTrue(content.contains("GZ log content"));

        Files.deleteIfExists(gzFile);
    }

    @Test
    void testViewLogsByDate_LogNotFound() {
        String date = "2000-01-01";
        assertThrows(ResourceNotFoundException.class, () -> logService.viewLogsByDate(date));
    }

    @Test
    void testViewLogsByDate_InvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> logService.viewLogsByDate("invalid-date"));
    }

    @Test
    void testViewLogsByDate_CorruptedGz() throws IOException {
        String date = "2022-01-02";
        Path gzFile = Paths.get("logs/online-store.log." + date + ".0.gz");
        Files.createDirectories(gzFile.getParent());
        Files.write(gzFile, "corrupt".getBytes());

        assertThrows(LogReadException.class, () -> logService.viewLogsByDate(date));

        Files.deleteIfExists(gzFile);
    }
}
