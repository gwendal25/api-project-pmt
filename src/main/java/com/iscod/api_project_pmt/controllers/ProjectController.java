package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.ProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
