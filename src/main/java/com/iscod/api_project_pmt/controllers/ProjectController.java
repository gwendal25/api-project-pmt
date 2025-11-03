package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.ProjectDto;
import com.iscod.api_project_pmt.dtos.ProjectRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(projectMapper.toDto(project));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> CreateProject(@RequestBody ProjectRequest projectRequest, UriComponentsBuilder uriBuilder) {
        Project project = projectMapper.toProject(projectRequest);
        projectRepository.save(project);
        ProjectDto projectDto = projectMapper.toDto(project);
        var uri = uriBuilder.path("/projects/{id}").buildAndExpand(projectDto.getId()).toUri();
        return ResponseEntity.created(uri).body(projectDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> UpdateProject(@PathVariable Long id, @RequestBody ProjectRequest projectRequest) {
        Project project = projectRepository.findById(id).orElse(null);

        if(project == null) {
            return ResponseEntity.notFound().build();
        }

        projectMapper.update(projectRequest, project);
        projectRepository.save(project);
        return ResponseEntity.ok(projectMapper.toDto(project));
    }
}
