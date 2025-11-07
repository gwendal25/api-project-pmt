package com.iscod.api_project_pmt.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SimpleTaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskPriority taskPriority;
    private TaskStatus taskStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
