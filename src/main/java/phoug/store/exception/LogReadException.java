package phoug.store.exception;

public class LogReadException extends RuntimeException {
    public LogReadException(String message) {
        super(message);
    }

    public LogReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
