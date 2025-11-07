package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.SimpleProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SimpleProjectMapper {
    SimpleProjectDto toDto(Project project);
}
