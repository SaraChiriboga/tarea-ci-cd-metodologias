package com.udla.todolist.service.impl;

import com.udla.todolist.exception.TaskNotFoundException;
import com.udla.todolist.model.Task;
import com.udla.todolist.repository.TaskRepository;
import com.udla.todolist.service.TaskService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link TaskService} sobre {@link TaskRepository}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task create(Task task) {
        task.setId(null);
        task.setCreatedAt(null);
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Task update(Long id, Task task) {
        Task existing = findById(id);
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setCompleted(task.isCompleted());
        return taskRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
