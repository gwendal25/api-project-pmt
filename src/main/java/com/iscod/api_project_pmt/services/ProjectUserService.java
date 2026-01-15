package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;

public interface ProjectUserService {
    ProjectUser getByProjectAndUser(Project project, User user);

    ProjectUserRoleDto getProjectUserRoleDto(Project project);
}
