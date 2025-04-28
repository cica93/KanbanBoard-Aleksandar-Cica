// package com.example.Kanban.Board.tasks;

// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.Mockito.when;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.web.servlet.MockMvc;
// import com.example.Kanban.Board.dto.TaskDTO;
// import com.example.Kanban.Board.model.User;
// import com.example.Kanban.Board.repository.TaskRepository;
// import com.example.Kanban.Board.repository.UserRepository;
// import com.example.Kanban.Board.service.TaskService;
// import com.example.Kanban.Board.utilities.TaskConverter;

// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class TaskControllerTest {
  
//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private TaskService taskService;

//     @SuppressWarnings("removal")
//     @MockBean
//     private TaskRepository taskRepository;

//     @SuppressWarnings("removal")
//     @MockBean
//     private UserRepository userRepository;

//     private User defaultUser = new User();

//     @BeforeEach
//     void setUp() {
     
//         when(userRepository.findByToken("some-token")).thenReturn(Optional.of(defaultUser));
//         when(userRepository.countByIdIn(Set.of(1L))).thenReturn(1L);
      
//         TaskDTO mockTask = new TaskDTO();
//         mockTask.setId(1L);
//         mockTask.setDescription("description");
//         mockTask.setTitle("title");
//         mockTask.setTaskStatus("TO_DO");
//         mockTask.setTaskPriority("LOW");

//         // Mock taskService behavior
//         when(taskRepository.findByIdAndDeletedFalse(1L))
//             .thenReturn(Optional.of(new TaskConverter().convertDTOModelToModel(mockTask)));
//         when(taskRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());
//         when(taskService.getById(1L)).thenReturn(ResponseEntity.ok(mockTask));
//         when(taskService.get(PageRequest.of(1,10), null)).thenReturn(ResponseEntity.ok(List.of(mockTask)));
//     }

//     @Test
//     void getTaskFromRepo() throws Exception {
//       ResponseEntity<TaskDTO> response = taskService.getById(2L);
//       assertNotNull(response);
//       assertEquals(200, response.getStatusCode().value());
//       assertEquals(1L, response.getBody().getId());
//       assertEquals("title", response.getBody().getTitle());
//     }

//     @Test
//     void testGetTask() throws Exception {
//         TaskDTO task = new TaskDTO();
//         task.setId(1L);
//         task.setTitle("Test Title");
//         when(taskService.getById(1L)).thenReturn(ResponseEntity.ok(task));

//         mockMvc.perform(get("/app/tasks/1").header("skip-user-interceptor", "skip"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(1L));
//     }
// }
