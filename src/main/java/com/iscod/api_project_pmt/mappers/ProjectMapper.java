package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectDto;
import com.iscod.api_project_pmt.dtos.project.ProjectRequest;
import com.iscod.api_project_pmt.dtos.task.ProjectTaskDto;
import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProjectTaskMapper projectTaskMapper;

    public ProjectDto toDto(Project project, User user, UserRole role) {
        ProjectDto projectDto = toPartialDto(project);
        projectDto.setUserRole(role);
        List<UserDto> users = project.getUsers().stream()
                .map(ProjectUser::getUser)
                .map(userProject -> userMapper.toDto(userProject))
                .toList();
        projectDto.setUsers(users);
        List<ProjectTaskDto> tasks = project.getTasks().stream()
                .map(task -> toProjectTaskDto(task, user))
                .toList();
        projectDto.setTasks(tasks);
        return projectDto;
    }

    @Mapping(target="tasks", ignore=true)
    @Mapping(target="users", ignore=true)
    @Mapping(target="userRole", ignore=true)
    public abstract ProjectDto toPartialDto(Project project);

    public abstract Project toProject(ProjectRequest projectRequest);

    public abstract void update(ProjectRequest projectRequest, @MappingTarget Project project);

    @Mapping(source="user.id", target="id")
    @Mapping(source="user.name", target="name")
    @Mapping(source="user.email", target="email")
    public abstract UserDto projectUserTouserDto(ProjectUser projectUser);

    public ProjectTaskDto toProjectTaskDto(Task task, User user) {
        ProjectTaskDto projectTaskDto = projectTaskMapper.toDto(task);
        projectTaskDto.setNotified(task.hasNotificationUser(user));
        return projectTaskDto;
    }
}
