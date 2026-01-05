package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.task.ProjectTaskDto;
import com.iscod.api_project_pmt.entities.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectTaskMapper {
    ProjectTaskDto toDto(Task task);
    List<ProjectTaskDto> toDtoList(List<Task> tasks);
}
