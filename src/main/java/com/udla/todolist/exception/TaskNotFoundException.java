package com.udla.todolist.exception;

/**
 * Se lanza cuando no existe una tarea con el ID solicitado.
 * El GlobalExceptionHandler (Persona 3) debe traducirla a HTTP 404.
 */
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("No se encontró la tarea con id " + taskId);
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}
