package com.iscod.api_project_pmt.dtos.projectuser;

import com.iscod.api_project_pmt.dtos.project.SimpleProjectDto;
import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectUserDto {
    private SimpleProjectDto project;
    private UserDto user;
    private UserRole role;
}
