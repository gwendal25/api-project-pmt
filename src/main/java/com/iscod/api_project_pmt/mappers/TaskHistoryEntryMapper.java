package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.task.TaskHistoryEntryDto;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskHistoryEntryMapper {
    TaskHistoryEntryDto toDto(TaskHistoryEntry taskHistoryEntry);

    @Mapping(target="id", ignore = true)
    @Mapping(target="task", ignore = true)
    TaskHistoryEntry toTaskHistoryEntry(Task task);
}
