package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectWithUserRolesDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SimpleProjectWithUserRolesMapper {
    public List<ProjectWithUserRolesDto> toDtoList(User user) {
        return user.getProjects().stream()
                .map(projectUser -> new ProjectWithUserRolesDto(
                        projectUser.getProject().getId(),
                        projectUser.getProject().getName(),
                        projectUser.getProject().getDescription(),
                        projectUser.getProject().getStartDate(),
                        projectUser.getRole()))
                .toList();
    }
}
