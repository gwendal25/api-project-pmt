package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.SimpleTaskDto;
import com.iscod.api_project_pmt.entities.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SimpleTaskMapper {
    SimpleTaskDto toDto(Task task);
}
