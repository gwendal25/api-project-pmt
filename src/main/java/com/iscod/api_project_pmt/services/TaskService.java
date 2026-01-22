package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.Task;

public interface TaskService {
    Task save(TaskRequest taskRequest, Project project);

    Task getTaskById(Long id);

    TaskDto getTaskDto(Task task);

    SimpleTaskDto getSimpleTaskDto(Task task);
}
