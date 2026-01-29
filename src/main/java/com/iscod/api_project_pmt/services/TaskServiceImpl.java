package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.task.ProjectTaskDto;
import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.mappers.SimpleTaskMapper;
import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskServiceImpl implements TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    SimpleTaskMapper simpleTaskMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    TaskHistoryEntryService taskHistoryEntryService;

    @Override
    public Task save(TaskRequest taskRequest, Project project) {
        Task task = taskMapper.toTask(taskRequest);
        task.setProject(project);
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public TaskDto getTaskDto(Task task) {
        return taskMapper.toDtoWithHistory(task);
    }

    @Override
    public SimpleTaskDto getSimpleTaskDto(Task task) {
        return simpleTaskMapper.toDto(task);
    }

    @Override
    public ProjectTaskDto getProjectTaskDto(Task task, User user) {
        return projectMapper.toProjectTaskDto(task, user);
    }

    @Override
    public Task addTaskHistoryEntry(TaskRequest taskRequest, Task task) {
        Task newTask = new Task();
        taskMapper.update(taskRequest, newTask);
        if(!newTask.equals(task)) {
            TaskHistoryEntry taskHistoryEntry = taskHistoryEntryService.save(task);
            taskMapper.update(taskRequest, task);
            task.addTaskHistoryEntry(taskHistoryEntry);
            return taskRepository.save(task);
        }
        return task;
    }

    @Override
    public Task addUser(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    @Override
    public Task removeUser(Task task) {
        task.setUser(null);
        return taskRepository.save(task);
    }

    @Override
    public void addNotificationUser(Task task, User user) {
        task.addNotificationUser(user);
        taskRepository.save(task);
    }

    @Override
    public void removeNotificationUser(Task task, User user) {
        task.removeNotificationUser(user);
        taskRepository.save(task);
    }

}
