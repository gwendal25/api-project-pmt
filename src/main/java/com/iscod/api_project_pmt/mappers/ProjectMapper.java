package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.ProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);
}
