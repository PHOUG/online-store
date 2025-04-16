package phoug.store.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LogTask {
    private String id;
    private String status;
    private String date;
    private String filePath;
    private String errorMessage;
    private LocalDateTime createdAt;

    public LogTask(String id, String status, String date, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.date = date;
        this.createdAt = createdAt;
    }
}