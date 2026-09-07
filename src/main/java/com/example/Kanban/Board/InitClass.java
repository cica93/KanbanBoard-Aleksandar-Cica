package com.example.Kanban.Board;

import java.util.ArrayList;
import java.util.List;

import com.example.Kanban.Board.configuration.PasswordEncoder;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.service.TaskService;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InitClass {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitClass(TaskService taskService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


      @PostConstruct
      private void init() {
          if (userRepository.count() == 0) {
              List<User> users = new ArrayList<>();
              User user1 = createUser("Pera Peric", "pera@gmail.com", "Pera123!");
              users.add(user1);
              User user2 = createUser("Zika Zikic", "zika@gmail.com", "Zika123!");
              users.add(user2);
              User user3 = createUser("Milica Milic", "milica@gmail.com", "Milica123!");
              users.add(user3);
              for (int i = 0; i < 100; i++) {
                  int random = (int) (Math.random() * 3);
                  saveTask("Task " + i, "Task description " + i, i % 3 == 0 ? TaskStatus.TO_DO
                          : i % 3 == 1 ? TaskStatus.IN_PROGRESS : TaskStatus.DONE,
                          random == 0 ? TaskPriority.LOW : random == 1 ? TaskPriority.MED : TaskPriority.HIGH, i / 3, users);
              }
          }
      }
    

      private User createUser(String fullName, String email, String password) {
          User user = new User();
          user.setFullName(fullName);
          user.setEmail(email);
          user.setPassword(passwordEncoder.encode(password));
          return userRepository.save(user);
      }
    
    private void saveTask(String title, String description, TaskStatus taskStatus, TaskPriority taskPriority, int task_order,
              List<User> users) {
          Task task = new Task();
          task.setDescription(description);
          task.setTaskOrder(task_order);
          task.setTitle(title);
          task.setTaskPriority(taskPriority);
          task.setTaskStatus(taskStatus);
          task.setUsers(users);
        task.setCreatedBy(users.get(0).getEmail());
        taskService.saveTask(users.get(0).getEmail(), task);

      }
    
}
