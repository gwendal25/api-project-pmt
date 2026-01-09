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
        ProjectDto projectDto = new ProjectDto(project.getId(), project.getName(), project.getDescription(), project.getStartDate());
        projectDto.setUserRole(role);
        List<UserDto> users = new ArrayList<>(project.getUsers().stream()
                .map(projectUser -> userMapper.toDto(projectUser.getUser()))
                .toList());
        projectDto.setUsers(users);
        List<ProjectTaskDto> tasks = new ArrayList<>(project.getTasks().stream()
                .map(task -> toProjectTaskDto(task, user))
                .toList());
        projectDto.setTasks(tasks);
        return projectDto;
    }

    public abstract Project toProject(ProjectRequest projectRequest);
    public abstract void update(ProjectRequest projectRequest, @MappingTarget Project project);
    @Mapping(source="user.id", target="id")
    @Mapping(source="user.name", target="name")
    @Mapping(source="user.email", target="email")
    public abstract UserDto projectUserTouserDto(ProjectUser projectUser);
    public abstract List<UserDto> projectUserListToUserDto(List<ProjectUser> projectUserList);

    public ProjectTaskDto toProjectTaskDto(Task task, User user) {
        ProjectTaskDto projectTaskDto = projectTaskMapper.toDto(task);
        projectTaskDto.setNotified(task.hasNotificationUser(user));
        return projectTaskDto;
    }
}
