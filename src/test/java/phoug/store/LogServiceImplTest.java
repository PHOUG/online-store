package phoug.store.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import phoug.store.exception.LogReadException;
import phoug.store.exception.ResourceNotFoundException;
import phoug.store.exception.TaskNotFoundException;
import phoug.store.model.LogTask;
import phoug.store.service.impl.LogServiceImpl;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceImplTest {

    @InjectMocks
    private LogServiceImpl logService;

    private LogTask completedTask;
    private LogTask pendingTask;
    private String completedTaskId;
    private String pendingTaskId;

    @BeforeEach
    void setUp() throws Exception {
        logService = new LogServiceImpl();

        completedTaskId = UUID.randomUUID().toString();
        pendingTaskId = UUID.randomUUID().toString();

        // Создаём файл в директории logs/
        Path logsDir = Paths.get("logs");
        Files.createDirectories(logsDir);
        Path completedFile = logsDir.resolve("completed.log");
        Files.writeString(completedFile, "Completed log content");

        completedTask = new LogTask(completedTaskId, "COMPLETED", "2024-04-01", LocalDateTime.now());
        completedTask.setFilePath("logs/completed.log"); // относительный путь

        Field tasksField = LogServiceImpl.class.getDeclaredField("tasks");
        tasksField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<String, LogTask> tasks = (java.util.Map<String, LogTask>) tasksField.get(logService);
        tasks.put(completedTaskId, completedTask);

        pendingTask = new LogTask(pendingTaskId, "PENDING", "2024-04-01", LocalDateTime.now());
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
    void testDownloadLogFile_NotCompleted() {
        ResponseEntity<Resource> response = logService.downloadLogFile(pendingTaskId);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDownloadLogFile_InvalidPath() {
        completedTask.setFilePath("/etc/passwd");
        ResponseEntity<Resource> response = logService.downloadLogFile(completedTaskId);
        assertEquals(400, response.getStatusCodeValue());
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
