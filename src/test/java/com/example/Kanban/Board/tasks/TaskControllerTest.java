// package com.example.Kanban.Board.tasks;

// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;

// import java.util.Collections;
// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.http.ResponseEntity;

// import com.example.Kanban.Board.dto.TaskDTO;
// import com.example.Kanban.Board.dto.UserDTO;
// import com.example.Kanban.Board.model.User;
// import com.example.Kanban.Board.service.TaskService;

// class TaskControllerTest {

//     @Test
//     void taskServiceCanBeMocked() throws Exception {
//         TaskService taskService = mock(TaskService.class);
//         TaskDTO taskDTO = new TaskDTO();
//         List<TaskDTO> tasks = Collections.singletonList(taskDTO);

//         when(taskService.get(org.springframework.data.domain.PageRequest.of(0, 20), "test"))
//                 .thenReturn(ResponseEntity.ok(tasks));

//         ResponseEntity<List<TaskDTO>> response = taskService.get(org.springframework.data.domain.PageRequest.of(0, 20), "test");
//         org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatusCode().value());
//     }

//     @Test
//     void createTaskResponseCanBeMocked() throws Exception {
//         TaskService taskService = mock(TaskService.class);
//         TaskDTO taskDTO = new TaskDTO();
//         taskDTO.setTitle("Test Task");
//         taskDTO.setDescription("This is a test task.");
//         UserDTO user = new UserDTO();
//         user.setId(1L);
//         taskDTO.setUsers(List.of(user));

//         when(taskService.create(new User(), taskDTO)).thenReturn(ResponseEntity.ok(taskDTO));

//         ResponseEntity<?> response = taskService.create(new User(), taskDTO);
//         org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatusCode().value());
//     }
// }
