package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;

public interface ProjectUserService {
    ProjectUser getByProjectAndUser(Project project, User user);

    ProjectUserRoleDto getProjectUserRoleDto(Project project);

    ProjectUser save(Project project, User user, UserRole role);

    ProjectUserDto getProjectUserDto(ProjectUser projectUser);

    void updateUserRole(ProjectUser projectUser, UserRole role);
}
