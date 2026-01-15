package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.ProjectDto;
import com.iscod.api_project_pmt.dtos.project.ProjectWithUserRolesDto;
import com.iscod.api_project_pmt.dtos.project.SimpleProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.mappers.SimpleProjectMapper;
import com.iscod.api_project_pmt.mappers.SimpleProjectWithUserRolesMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    SimpleProjectMapper simpleProjectMapper;

    @Autowired
    SimpleProjectWithUserRolesMapper simpleProjectWithUserRolesMapper;

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public ProjectDto getProjectDto(Project project, User user, ProjectUser projectUser) {
        return projectMapper.toDto(project, user, projectUser.getRole());
    }

    @Override
    public List<SimpleProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(simpleProjectMapper::toDto)
                .toList();
    }

    @Override
    public List<ProjectWithUserRolesDto> getAllProjectsByUser(User user) {
        return simpleProjectWithUserRolesMapper.toDtoList(user);
    }
}
