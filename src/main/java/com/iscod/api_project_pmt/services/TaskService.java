package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.task.ProjectTaskDto;
import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;

public interface TaskService {
    Task save(TaskRequest taskRequest, Project project);

    Task getTaskById(Long id);

    TaskDto getTaskDto(Task task);

    SimpleTaskDto getSimpleTaskDto(Task task);

    ProjectTaskDto getProjectTaskDto(Task task, User user);

    Task addTaskHistoryEntry(TaskRequest taskRequest, Task task);

    Task addUser(Task task, User user);

    void addNotificationUser(Task task, User user);

    void removeNotificationUser(Task task, User user);
}
