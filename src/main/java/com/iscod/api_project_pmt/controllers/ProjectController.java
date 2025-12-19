package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.*;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.mappers.SimpleProjectMapper;
import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper projectMapper;
    private final SimpleProjectMapper simpleProjectMapper;
    private final TaskMapper taskMapper;

    @GetMapping
    public List<SimpleProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(simpleProjectMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id, @RequestHeader("Authorization") String userIdStr) {
        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            return ResponseEntity.notFound().build();
        }

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(projectMapper.toDto(project, user));
    }

    @PostMapping
    public ResponseEntity<SimpleProjectDto> CreateProject(@RequestBody ProjectRequest projectRequest, UriComponentsBuilder uriBuilder) {
        User user = userRepository.findByName(projectRequest.getUsername()).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        Project project = projectMapper.toProject(projectRequest);
        project = projectRepository.save(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.ADMIN);
        projectUserRepository.save(projectUser);

        SimpleProjectDto projectDto = simpleProjectMapper.toDto(project);
        var uri = uriBuilder.path("/projects/{id}").buildAndExpand(projectDto.getId()).toUri();
        return ResponseEntity.created(uri).body(projectDto);
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskDto> CreateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest, UriComponentsBuilder uriBuilder) {
        Project project = projectRepository.findById(id).orElse(null);

        if(project == null) {
            return ResponseEntity.notFound().build();
        }

        Task task = taskMapper.toTask(taskRequest);
        task.setProject(project);
        project.addTask(task);
        taskRepository.save(task);
        projectRepository.save(project);
        TaskDto taskDto = taskMapper.toDto(task);
        var uri = uriBuilder.path("/tasks/{id}").buildAndExpand(taskDto.getId()).toUri();
        return ResponseEntity.created(uri).body(taskDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimpleProjectDto> UpdateProject(@PathVariable Long id, @RequestBody ProjectRequest projectRequest) {
        Project project = projectRepository.findById(id).orElse(null);

        if(project == null) {
            return ResponseEntity.notFound().build();
        }

        projectMapper.update(projectRequest, project);
        projectRepository.save(project);
        return ResponseEntity.ok(simpleProjectMapper.toDto(project));
    }
}
