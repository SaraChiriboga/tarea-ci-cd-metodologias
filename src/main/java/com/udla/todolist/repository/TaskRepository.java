package com.udla.todolist.repository;

import com.udla.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de {@link Task}. Hereda de JpaRepository los métodos
 * save, findAll, findById, existsById, deleteById, etc.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
