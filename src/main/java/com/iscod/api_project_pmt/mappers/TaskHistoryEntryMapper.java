package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.TaskHistoryEntryDto;
import com.iscod.api_project_pmt.entities.TaskHistoryEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskHistoryEntryMapper {
    TaskHistoryEntryDto toDto(TaskHistoryEntry taskHistoryEntry);
}
