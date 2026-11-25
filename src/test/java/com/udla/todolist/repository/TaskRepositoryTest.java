package com.udla.todolist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.udla.todolist.model.Task;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

/**
 * Pruebas de {@link TaskRepository} contra una base H2 en memoria.
 */
@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("save: asigna id, createdAt y completed=false por defecto")
    void saveAssignsIdAndCreatedAt() {
        Task saved = taskRepository.save(Task.builder().title("Persistir").build());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("findById y deleteById funcionan sobre H2")
    void findAndDelete() {
        Task saved = taskRepository.save(Task.builder().title("Borrar").description("temporal").build());

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("temporal");

        taskRepository.deleteById(saved.getId());
        assertThat(taskRepository.existsById(saved.getId())).isFalse();
    }
}
