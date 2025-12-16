package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.ProjectDto;
import com.iscod.api_project_pmt.dtos.ProjectRequest;
import com.iscod.api_project_pmt.dtos.UserDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toProject(ProjectRequest projectRequest);
    void update(ProjectRequest projectRequest, @MappingTarget Project project);
    @Mapping(source="user.id", target="id")
    @Mapping(source="user.name", target="name")
    @Mapping(source="user.email", target="email")
    UserDto projectUserTouserDto(ProjectUser projectUser);
    List<UserDto> projectUserListToUserDto(List<ProjectUser> projectUserList);
}
