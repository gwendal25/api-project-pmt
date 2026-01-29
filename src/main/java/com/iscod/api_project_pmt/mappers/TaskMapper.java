package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.task.TaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskHistoryEntryDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {
    @Autowired
    TaskHistoryEntryMapper taskHistoryEntryMapper;

    public abstract TaskDto toDto(Task task);

    @Mapping(target="taskHistoryEntries", ignore = true)
    public abstract TaskDto toPartialDto(Task task);

    public abstract Task toTask(TaskRequest taskRequest);

    public abstract void update(TaskRequest taskRequest, @MappingTarget Task task);

        public TaskDto toDtoWithHistory(Task task) {
        TaskDto taskDto = toPartialDto(task);
        List<TaskHistoryEntryDto> taskList = new java.util.ArrayList<>(task.getTaskHistoryEntries().stream()
                .map(taskHistoryEntry -> taskHistoryEntryMapper.toDto(taskHistoryEntry))
                .toList());
        taskList.sort((t1, t2) -> t2.getEditDate().compareTo(t1.getEditDate()));
        taskDto.setTaskHistoryEntries(taskList);
    return taskDto;
    }
}
