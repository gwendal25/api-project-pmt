package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.dtos.user.UserRoleDto;
import com.iscod.api_project_pmt.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProjectUserRoleMapper {
    @Autowired
    UserRoleMapper userRoleMapper;

    public ProjectUserRoleDto toProjectUserRoleDto(Project project) {
        ProjectUserRoleDto projectUserRoleDto = toPartialUserRoleDto(project);
        List<UserRoleDto> users = project.getUsers().stream()
                .map(projectUser -> userRoleMapper.toUserRoleDto(projectUser))
                .toList();
        projectUserRoleDto.setUsers(users);
        return projectUserRoleDto;
    }

    @Mapping(target="users", ignore=true)
    public abstract ProjectUserRoleDto toPartialUserRoleDto(Project project);
}
