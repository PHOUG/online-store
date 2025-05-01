package phoug.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import phoug.store.model.LogTask;
import phoug.store.service.LogService;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "API для управления логами")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/status/{taskId}")
    @Operation(summary = "Возвращение статуса задачи",
            description = "Возвращает статус задачи генерации логов по taskId")
    public Map<String, Object> getTaskStatus(@PathVariable String taskId) {
        LogTask task = logService.getTaskStatus(taskId);

        Map<String, Object> response = new HashMap<>();
        response.put("taskId", task.getId());
        response.put("status", task.getStatus());
        response.put("createdAt", task.getCreatedAt());

        if (task.getErrorMessage() != null) {
            response.put("errorMessage", task.getErrorMessage());
        }

        return response;
    }

    @GetMapping("/download/{taskId}")
    @Operation(summary = "Скачать сгенерированный log-файл",
            description = "Позволяет скачать сгенерированный лог-файл, если задача завершена")
    public ResponseEntity<Resource> downloadLogFile(@PathVariable String taskId) {
        return logService.downloadLogFile(taskId);
    }

    @GetMapping("/view")
    @Operation(summary = "Просмотр логов",
            description = "Позволяет просмотреть содержимое "
                    + "логов за указанную дату как обычный текст")
    public String viewLogs(@RequestParam String date) {
        return logService.viewLogsByDate(date);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Запуск асинхронной задачи",
            description = "Создаёт фоновую задачу по генерации "
                    + "лог-файла за указанную дату и возвращает её ID"
    )
    public Map<String, String> startLogTask(@RequestParam String date) {
        try {
            String taskId = logService.createLogTask(date);
            return Map.of("taskId", taskId);
        } catch (IllegalArgumentException ex) {
            // неверный формат даты
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }

    }
}