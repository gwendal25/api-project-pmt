package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserDto;
import com.iscod.api_project_pmt.entities.ProjectUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectUserMapper {
    ProjectUserDto toDto(ProjectUser projectUser);
}
