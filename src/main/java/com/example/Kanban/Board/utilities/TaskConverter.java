package com.example.Kanban.Board.utilities;

import java.util.Arrays;

import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;

public class TaskConverter extends GenericConverter<Task, TaskDTO> {

    private UserConverter userConverter = new UserConverter();

    @Override
    public Task convertDTOModelToModel(TaskDTO dtoModel) throws NotValidTaskStatusException, NotValidTaskPriorityException {
        Task task = new Task();
        task.setId(dtoModel.getId());
        task.setVersion(dtoModel.getVersion() == null ? Integer.valueOf(0): dtoModel.getVersion());
        task.setDescription(dtoModel.getDescription());
        task.setTitle(dtoModel.getTitle());
        task.setTaskStatus(convertStringToTaskStatus(dtoModel.getTaskStatus()));
        task.setTaskPriority(convertStringToTaskPriority(dtoModel.getTaskPriority()));
        task.setUsers(userConverter.convertListOfDTOModelsTOModel(dtoModel.getUsers()));
        return task;
    }

    @Override
    public TaskDTO convertModelToDTOModel(Task model) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(model.getId());
        taskDTO.setTaskStatus(model.getTaskStatus().name());
        taskDTO.setTaskPriority(model.getTaskPriority().name());
        taskDTO.setDescription(model.getDescription());
        taskDTO.setTitle(model.getTitle());
        taskDTO.setUpdatedBy(model.getUpdatedBy());
        taskDTO.setCreatedBy(model.getCreatedBy());
        taskDTO.setVersion(model.getVersion());
        taskDTO.setUsers(userConverter.convertListOfModelsToDTOModel(model.getUsers()));
        return taskDTO;
    }

    public TaskStatus convertStringToTaskStatus(String taskStatus) throws NotValidTaskStatusException {
        boolean isValid = Arrays.asList(TaskStatus.values()).stream()
                .anyMatch(p -> p.name().equals(taskStatus));
        if (!isValid) {
            throw new NotValidTaskStatusException(taskStatus + " is not valid value for task status");
        }

        return TaskStatus.valueOf(taskStatus);
    }

    public TaskPriority convertStringToTaskPriority(String taskPriority) throws NotValidTaskPriorityException {
        boolean isValid = Arrays.asList(TaskPriority.values()).stream()
                .anyMatch(p -> p.name().equals(taskPriority));
        if (!isValid) {
            throw new NotValidTaskPriorityException(taskPriority + " is not valid value for task priority");
        }

        return TaskPriority.valueOf(taskPriority);
    }
    
}
