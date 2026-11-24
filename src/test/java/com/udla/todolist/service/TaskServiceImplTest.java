package com.udla.todolist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.udla.todolist.exception.TaskNotFoundException;
import com.udla.todolist.model.Task;
import com.udla.todolist.repository.TaskRepository;
import com.udla.todolist.service.impl.TaskServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de {@link TaskServiceImpl} usando un repositorio simulado (Mockito).
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        existingTask = Task.builder()
                .id(1L)
                .title("Estudiar CI/CD")
                .description("Repasar GitLab pipelines")
                .completed(false)
                .createdAt(LocalDateTime.of(2026, 11, 24, 10, 0))
                .build();
    }

    @Test
    @DisplayName("create: ignora id y createdAt enviados y guarda la tarea")
    void createIgnoresIdAndCreatedAt() {
        Task input = Task.builder()
                .id(99L)
                .title("Nueva tarea")
                .createdAt(LocalDateTime.of(2000, 1, 1, 0, 0))
                .build();
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        Task result = taskService.create(input);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Nueva tarea");
        assertThat(result.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("findAll: devuelve todas las tareas del repositorio")
    void findAllReturnsAllTasks() {
        Task other = Task.builder().id(2L).title("Otra").build();
        when(taskRepository.findAll()).thenReturn(List.of(existingTask, other));

        List<Task> result = taskService.findAll();

        assertThat(result).hasSize(2).containsExactly(existingTask, other);
    }

    @Test
    @DisplayName("findAll: devuelve lista vacía si no hay tareas")
    void findAllReturnsEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        assertThat(taskService.findAll()).isEmpty();
    }

    @Test
    @DisplayName("findById: devuelve la tarea cuando existe")
    void findByIdReturnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        Task result = taskService.findById(1L);

        assertThat(result).isSameAs(existingTask);
    }

    @Test
    @DisplayName("findById: lanza TaskNotFoundException si el id no existe")
    void findByIdThrowsWhenMissing() {
        when(taskRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(42L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("42")
                .satisfies(ex -> assertThat(((TaskNotFoundException) ex).getTaskId()).isEqualTo(42L));
    }

    @Test
    @DisplayName("update: reemplaza title, description y completed sin tocar id ni createdAt")
    void updateModifiesFields() {
        LocalDateTime originalCreatedAt = existingTask.getCreatedAt();
        Task changes = Task.builder()
                .id(500L)
                .title("Título editado")
                .description("Nueva descripción")
                .completed(true)
                .createdAt(LocalDateTime.of(2000, 1, 1, 0, 0))
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.update(1L, changes);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Título editado");
        assertThat(result.getDescription()).isEqualTo("Nueva descripción");
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    @DisplayName("update: lanza TaskNotFoundException y no guarda si el id no existe")
    void updateThrowsWhenMissing() {
        when(taskRepository.findById(42L)).thenReturn(Optional.empty());
        Task changes = Task.builder().title("x").build();

        assertThatThrownBy(() -> taskService.update(42L, changes))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("delete: elimina la tarea cuando existe")
    void deleteRemovesTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete: lanza TaskNotFoundException y no elimina si el id no existe")
    void deleteThrowsWhenMissing() {
        when(taskRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(42L))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).deleteById(any());
    }
}
