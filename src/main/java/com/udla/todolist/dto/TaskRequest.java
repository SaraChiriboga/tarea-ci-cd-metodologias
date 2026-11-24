package com.udla.todolist.dto;

import com.udla.todolist.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de las peticiones POST y PUT /api/tasks.
 */
@Schema(description = "Datos para crear o actualizar una tarea")
public record TaskRequest(

        @Schema(description = "Título de la tarea", example = "Estudiar para el examen", maxLength = 100)
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar los 100 caracteres")
        String title,

        @Schema(description = "Descripción opcional", example = "Repasar CI/CD y Git Flow", maxLength = 500)
        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String description,

        @Schema(description = "Indica si la tarea está completada (por defecto false)", example = "false")
        Boolean completed) {

    /** Convierte el DTO en entidad. Si completed no viene, queda en false. */
    public Task toEntity() {
        return Task.builder()
                .title(title.trim())
                .description(description)
                .completed(Boolean.TRUE.equals(completed))
                .build();
    }
}
