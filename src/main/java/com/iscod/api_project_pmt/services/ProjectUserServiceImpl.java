package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.mappers.ProjectUserMapper;
import com.iscod.api_project_pmt.mappers.ProjectUserRoleMapper;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectUserServiceImpl implements ProjectUserService {
    @Autowired
    ProjectUserRepository projectUserRepository;

    @Autowired
    ProjectUserMapper projectUserMapper;

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

    @Override
    public ProjectUser save(Project project, User user, UserRole role) {
        return projectUserRepository.save(new ProjectUser(project, user, role));
    }

    @Override
    public ProjectUserDto getProjectUserDto(ProjectUser projectUser) {
        return projectUserMapper.toDto(projectUser);
    }

    @Override
    public void updateUserRole(ProjectUser projectUser, UserRole role) {
        projectUser.setRole(role);
        projectUserRepository.save(projectUser);
    }
}
