package com.iscod.api_project_pmt.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
}
