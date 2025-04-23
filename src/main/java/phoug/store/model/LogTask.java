package phoug.store.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LogTask {
    private String id;
    private String status;
    private String date;
    private String filePath;
    private String errorMessage;
    private LocalDateTime createdAt;

    public LogTask() {}

    public LogTask(String id, String status, String date, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.date = date;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}