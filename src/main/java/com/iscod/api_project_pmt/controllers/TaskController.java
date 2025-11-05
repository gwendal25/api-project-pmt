package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks")
public class TaskController {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


}
