package com.iscod.api_project_pmt.dtos.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iscod.api_project_pmt.dtos.user.UserRoleDto;
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
public class ProjectUserRoleDto {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    private List<UserRoleDto> users;

    public ProjectUserRoleDto(Long id, String name, String description, Date startDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }
}
