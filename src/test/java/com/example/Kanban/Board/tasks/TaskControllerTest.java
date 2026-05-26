package com.example.Kanban.Board.tasks;

import com.example.Kanban.Board.controller.TaskController;
import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.service.TaskService;
import com.example.Kanban.Board.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User(); // You may want to set some values if needed
    }

    @Test
    public void testGetTasks() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        List<TaskDTO> tasks = Collections.singletonList(taskDTO);
        when(taskService.get(any(PageRequest.class), anyString()))
                .thenReturn(ResponseEntity.ok(tasks));

        mockMvc.perform(get("/api/tasks")
                .param("description", "test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetById() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.getById(eq(1L))).thenReturn(ResponseEntity.ok(taskDTO));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.create(any(User.class), any(TaskDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Task\"}")) // minimal JSON
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateTask() throws Exception {
        when(taskService.update(any(User.class), eq(1L), any(TaskDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Task\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchTask() throws Exception {
        when(taskService.patch(any(User.class), eq(1L), any(TaskDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Partial update\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTask() throws Exception {
        when(taskService.delete(1L, 1))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
