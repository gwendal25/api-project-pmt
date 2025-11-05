package com.iscod.api_project_pmt.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskRequest {
    private String name;
    private String description;
    private TaskPriority taskPriority;
    private TaskStatus taskStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
}
