package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.Task;

public interface TaskService {
    Task save(TaskRequest taskRequest, Project project);

    SimpleTaskDto getSimpleTaskDto(Task task);
}
