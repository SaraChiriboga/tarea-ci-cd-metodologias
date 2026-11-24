package com.udla.todolist.dto;

import com.udla.todolist.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Representación de una tarea que se devuelve al cliente (la entidad no se expone).
 */
@Schema(description = "Tarea devuelta por la API")
public record TaskResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Estudiar para el examen") String title,
        @Schema(example = "Repasar CI/CD y Git Flow") String description,
        @Schema(example = "false") boolean completed,
        @Schema(example = "2026-11-24T10:15:30") LocalDateTime createdAt) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt());
    }
}
