package com.iscod.api_project_pmt.dtos.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iscod.api_project_pmt.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectWithUserRolesDto {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    private UserRole userRole;
}
