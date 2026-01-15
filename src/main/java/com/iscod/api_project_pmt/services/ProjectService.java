package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.ProjectDto;
import com.iscod.api_project_pmt.dtos.project.ProjectWithUserRolesDto;
import com.iscod.api_project_pmt.dtos.project.SimpleProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;

import java.util.List;

public interface ProjectService {
    Project getProjectById(Long id);

    ProjectDto getProjectDto(Project project, User user, ProjectUser projectUser);

    List<SimpleProjectDto> getAllProjects();

    List<ProjectWithUserRolesDto> getAllProjectsByUser(User user);
}
