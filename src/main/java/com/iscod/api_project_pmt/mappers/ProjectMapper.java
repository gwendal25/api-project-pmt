package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.ProjectDto;
import com.iscod.api_project_pmt.dtos.ProjectRequest;
import com.iscod.api_project_pmt.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toProject(ProjectRequest projectRequest);
    void update(ProjectRequest projectRequest, @MappingTarget Project project);
}
