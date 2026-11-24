package com.udla.todolist.controller;

import com.udla.todolist.dto.TaskRequest;
import com.udla.todolist.dto.TaskResponse;
import com.udla.todolist.model.Task;
import com.udla.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Endpoints REST del CRUD de tareas.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tareas", description = "CRUD de tareas por hacer")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Crear una tarea")
    @ApiResponse(responseCode = "201", description = "Tarea creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        Task created = taskService.create(request.toEntity());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(TaskResponse.from(created));
    }

    @GetMapping
    @Operation(summary = "Listar todas las tareas")
    @ApiResponse(responseCode = "200", description = "Lista de tareas (vacía si no hay ninguna)")
    public List<TaskResponse> findAll() {
        return taskService.findAll().stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una tarea por ID")
    @ApiResponse(responseCode = "200", description = "Tarea encontrada")
    @ApiResponse(responseCode = "404", description = "La tarea no existe")
    public TaskResponse findById(@PathVariable Long id) {
        return TaskResponse.from(taskService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarea")
    @ApiResponse(responseCode = "200", description = "Tarea actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "La tarea no existe")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return TaskResponse.from(taskService.update(id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarea")
    @ApiResponse(responseCode = "204", description = "Tarea eliminada")
    @ApiResponse(responseCode = "404", description = "La tarea no existe")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
