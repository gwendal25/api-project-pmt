package com.iscod.api_project_pmt.dtos.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskPriority taskPriority;
    private TaskStatus taskStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private List<TaskHistoryEntryDto> taskHistoryEntries;

    public TaskDto(Long id, String name, String description, TaskPriority taskPriority, TaskStatus taskStatus, Date endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskPriority = taskPriority;
        this.taskStatus = taskStatus;
        this.endDate = endDate;
    }
}
