package com.udla.todolist.service;

import com.udla.todolist.exception.TaskNotFoundException;
import com.udla.todolist.model.Task;
import java.util.List;

/**
 * Lógica de negocio del CRUD de tareas.
 *
 * <p>Contrato para la Persona 3 (controller) y la Persona 4 (pruebas):
 * <ul>
 *   <li>Recibe y devuelve entidades {@link Task}; el mapeo desde/hacia
 *       TaskRequest/TaskResponse se hace en la capa de controller/dto.</li>
 *   <li>Cualquier ID inexistente lanza {@link TaskNotFoundException} (→ 404).</li>
 *   <li>El servicio no valida campos: las validaciones (@NotBlank, @Size)
 *       van en el DTO con @Valid.</li>
 * </ul>
 */
public interface TaskService {

    /**
     * Crea una tarea nueva. Ignora el id y createdAt que traiga el objeto;
     * completed queda en false si no se indica.
     *
     * @return la tarea guardada, con id y createdAt asignados
     */
    Task create(Task task);

    /** @return todas las tareas (lista vacía si no hay ninguna) */
    List<Task> findAll();

    /**
     * @return la tarea con ese id
     * @throws TaskNotFoundException si no existe
     */
    Task findById(Long id);

    /**
     * Reemplaza title, description y completed de la tarea indicada.
     * id y createdAt no se modifican.
     *
     * @return la tarea actualizada
     * @throws TaskNotFoundException si no existe
     */
    Task update(Long id, Task task);

    /**
     * Elimina la tarea indicada.
     *
     * @throws TaskNotFoundException si no existe
     */
    void delete(Long id);
}
