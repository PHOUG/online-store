package phoug.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phoug.store.service.VisitCounterService;

@RestController
@RequestMapping("/visits")
@Tag(name = "Учёт посещений",
        description = "Операции для отслеживания и получения статистики посещений сайта")
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @PostMapping("/track")
    @Operation(summary = "Зафиксировать посещение URL",
            description = "Увеличивает счётчик посещений для указанного пути"
    )
    public ResponseEntity<Void> trackVisit(
            @Parameter(description = "Путь URL, например products/123")
            @RequestParam String path) {
        visitCounterService.recordVisit(path);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Получить количество посещений URL",
            description = "Возвращает количество посещений для заданного пути"
    )
    public ResponseEntity<Integer> getVisitCount(
            @Parameter(description = "Путь URL")
            @RequestParam String path) {
        int count = visitCounterService.getVisitCount(path);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить статистику всех посещений",
            description = "Возвращает карту всех URL и количества их посещений"
    )
    public ResponseEntity<Map<String, Integer>> getAllVisits() {
        return ResponseEntity.ok(visitCounterService.getAllVisits());
    }
}
