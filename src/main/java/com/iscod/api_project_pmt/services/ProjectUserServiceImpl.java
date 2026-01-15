package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.ProjectUserRoleMapper;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectUserServiceImpl implements ProjectUserService {
    @Autowired
    ProjectUserRepository projectUserRepository;

    @Autowired
    ProjectUserRoleMapper projectUserRoleMapper;

    @Override
    public ProjectUser getByProjectAndUser(Project project, User user) {
        return projectUserRepository.findByProjectAndUser(project, user).orElse(null);
    }

    @Override
    public ProjectUserRoleDto getProjectUserRoleDto(Project project) {
        return projectUserRoleMapper.toUserRoleDto(project);
    }
}
