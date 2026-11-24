package com.udla.todolist.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato JSON uniforme para todos los errores de la API.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details) {
}
