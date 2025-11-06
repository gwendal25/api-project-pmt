package com.iscod.api_project_pmt.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    private List<ProjectTaskDto> tasks;
}
