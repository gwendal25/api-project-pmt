package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.TaskDto;
import com.iscod.api_project_pmt.dtos.TaskRequest;
import com.iscod.api_project_pmt.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(Task task);
    Task toTask(TaskRequest taskRequest);
    void update(TaskRequest taskRequest, @MappingTarget Task task);
}
