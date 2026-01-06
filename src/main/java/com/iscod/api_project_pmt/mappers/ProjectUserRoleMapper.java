package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.dtos.user.UserRoleDto;
import com.iscod.api_project_pmt.entities.Project;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProjectUserRoleMapper {
    public ProjectUserRoleDto toDto(Project project) {
        ProjectUserRoleDto projectUserRoleDto = new ProjectUserRoleDto(project.getId(), project.getName(), project.getDescription(), project.getStartDate());
        List<UserRoleDto> users = new ArrayList<>(project.getUsers().stream()
                .map(projectUser -> new UserRoleDto(projectUser.getUser().getId(), projectUser.getUser().getName(), projectUser.getUser().getEmail(), projectUser.getRole()))
                .toList());
        projectUserRoleDto.setUsers(users);
        return projectUserRoleDto;
    }
}
