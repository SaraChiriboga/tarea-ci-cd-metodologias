package com.udla.todolist.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.udla.todolist.exception.TaskNotFoundException;
import com.udla.todolist.model.Task;
import com.udla.todolist.service.TaskService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas de la capa web: {@link TaskController} + GlobalExceptionHandler,
 * con el servicio simulado.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String BASE_URL = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private static Task sampleTask(Long id, String title, boolean completed) {
        return Task.builder()
                .id(id)
                .title(title)
                .description("Descripción de prueba")
                .completed(completed)
                .createdAt(LocalDateTime.of(2026, 11, 24, 10, 0))
                .build();
    }

    // ---------- POST ----------

    @Test
    @DisplayName("POST /api/tasks con datos válidos devuelve 201, Location y la tarea creada")
    void createReturns201() throws Exception {
        when(taskService.create(any(Task.class))).thenReturn(sampleTask(1L, "Estudiar", false));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Estudiar", "description": "Descripción de prueba"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/tasks/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Estudiar"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("POST /api/tasks sin título devuelve 400 con detalle de validación")
    void createWithoutTitleReturns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "   ", "description": "sin título"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value(BASE_URL))
                .andExpect(jsonPath("$.details", hasItem(containsString("title"))));

        verify(taskService, never()).create(any(Task.class));
    }

    @Test
    @DisplayName("POST /api/tasks con título de más de 100 caracteres devuelve 400")
    void createWithTooLongTitleReturns400() throws Exception {
        String longTitle = "a".repeat(101);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"" + longTitle + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem(containsString("100"))));
    }

    @Test
    @DisplayName("POST /api/tasks con JSON mal formado devuelve 400")
    void createWithMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // ---------- GET ----------

    @Test
    @DisplayName("GET /api/tasks devuelve 200 con la lista de tareas")
    void findAllReturns200() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(
                sampleTask(1L, "Uno", false),
                sampleTask(2L, "Dos", true)));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Uno"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} existente devuelve 200")
    void findByIdReturns200() throws Exception {
        when(taskService.findById(1L)).thenReturn(sampleTask(1L, "Uno", false));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Uno"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} inexistente devuelve 404 con JSON de error")
    void findByIdNotFoundReturns404() throws Exception {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("99")))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/99"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} con id no numérico devuelve 400")
    void findByIdWithInvalidIdReturns400() throws Exception {
        mockMvc.perform(get(BASE_URL + "/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("id")));
    }

    // ---------- PUT ----------

    @Test
    @DisplayName("PUT /api/tasks/{id} existente devuelve 200 con la tarea actualizada")
    void updateReturns200() throws Exception {
        when(taskService.update(eq(1L), any(Task.class))).thenReturn(sampleTask(1L, "Editada", true));

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Editada", "completed": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Editada"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} inexistente devuelve 404")
    void updateNotFoundReturns404() throws Exception {
        when(taskService.update(eq(99L), any(Task.class))).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Editada"}
                                """))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE ----------

    @Test
    @DisplayName("DELETE /api/tasks/{id} existente devuelve 204")
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(taskService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} inexistente devuelve 404")
    void deleteNotFoundReturns404() throws Exception {
        doThrow(new TaskNotFoundException(99L)).when(taskService).delete(99L);

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    // ---------- Error inesperado ----------

    @Test
    @DisplayName("Un error inesperado del servicio devuelve 500 con JSON uniforme")
    void unexpectedErrorReturns500() throws Exception {
        when(taskService.findAll()).thenThrow(new IllegalStateException("boom"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}
