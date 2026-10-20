package com.rpa.infojobs.robot.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, List<String>> fieldErrors
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, LocalDateTime.now(), null);
    }

    public ErrorResponse(int status, String error, String message, String path,
                         Map<String, List<String>> fieldErrors) {
        this(status, error, message, path, LocalDateTime.now(), fieldErrors);
    }
}
