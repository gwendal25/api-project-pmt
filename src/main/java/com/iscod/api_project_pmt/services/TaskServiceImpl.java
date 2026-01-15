package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.Task;
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

    @Override
    public Task save(TaskRequest taskRequest, Project project) {
        Task task = taskMapper.toTask(taskRequest);
        task.setProject(project);
        return taskRepository.save(task);
    }

    @Override
    public SimpleTaskDto getSimpleTaskDto(Task task) {
        return simpleTaskMapper.toDto(task);
    }
}
