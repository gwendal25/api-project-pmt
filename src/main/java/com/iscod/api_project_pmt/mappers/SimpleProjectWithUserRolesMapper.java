package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectWithUserRolesDto;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SimpleProjectWithUserRolesMapper {
    public List<ProjectWithUserRolesDto> toDtoList(User user) {
        return user.getProjects().stream()
                .map(this::toDto)
                .toList();
    }

    @Mapping(source="project.id", target="id")
    @Mapping(source="project.name", target="name")
    @Mapping(source="project.description", target="description")
    @Mapping(source="project.startDate", target="startDate")
    @Mapping(source="role", target="userRole")
    public abstract ProjectWithUserRolesDto toDto(ProjectUser projectUser);
}
