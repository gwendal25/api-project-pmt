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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectMapperTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProjectTaskMapper projectTaskMapper;

    private ProjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProjectMapperImpl();
        mapper.userMapper = userMapper;
        mapper.projectTaskMapper = projectTaskMapper;
    }

    @Test
    void testToPartialDto() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(new Date());

        // Act
        ProjectDto result = mapper.toPartialDto(project);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStartDate()).isEqualTo(project.getStartDate());
        // tasks, users, userRole should be null or empty as per mapping ignore
    }

    @Test
    void testToProject() {
        // Arrange
        ProjectRequest request = new ProjectRequest();
        request.setName("New Project");
        request.setDescription("New Description");
        request.setStartDate(new Date());

        // Act
        Project result = mapper.toProject(request);

        // Assert
        assertThat(result.getName()).isEqualTo("New Project");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getStartDate()).isEqualTo(request.getStartDate());
    }

    @Test
    void testUpdate() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Old Name");
        project.setDescription("Old Description");

        ProjectRequest request = new ProjectRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Description");
        request.setStartDate(new Date());

        // Act
        mapper.update(request, project);

        // Assert
        assertThat(project.getId()).isEqualTo(1L); // id should not change
        assertThat(project.getName()).isEqualTo("Updated Name");
        assertThat(project.getDescription()).isEqualTo("Updated Description");
        assertThat(project.getStartDate()).isEqualTo(request.getStartDate());
    }

    @Test
    void testProjectUserToUserDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        ProjectUser projectUser = new ProjectUser();
        projectUser.setUser(user);

        // Act
        UserDto result = mapper.projectUserTouserDto(projectUser);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testToProjectTaskDto() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setNotificationUsers(new HashSet<>()); // Initialize

        User user = new User();
        user.setId(2L);

        ProjectTaskDto taskDto = new ProjectTaskDto();
        taskDto.setId(1L);
        taskDto.setName("Test Task");

        when(projectTaskMapper.toDto(task)).thenReturn(taskDto);
        // task.notificationUsers is empty, so hasNotificationUser returns false

        // Act
        ProjectTaskDto result = mapper.toProjectTaskDto(task, user);

        // Assert
        assertThat(result).isEqualTo(taskDto);
        assertThat(result.isNotified()).isFalse();
    }

    @Test
    void testToDto() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        User user = new User();
        user.setId(2L);

        UserRole role = UserRole.ADMIN;

        // Mock users
        User user1 = new User();
        user1.setId(3L);
        ProjectUser pu1 = new ProjectUser();
        pu1.setUser(user1);
        project.setUsers(Set.of(pu1));

        UserDto userDto1 = new UserDto();
        userDto1.setId(3L);
        when(userMapper.toDto(user1)).thenReturn(userDto1);

        // Mock tasks
        Task task1 = new Task();
        task1.setId(4L);
        task1.setNotificationUsers(new HashSet<>()); // Initialize
        project.setTasks(Set.of(task1));

        ProjectTaskDto taskDto1 = new ProjectTaskDto();
        taskDto1.setId(4L);
        when(projectTaskMapper.toDto(task1)).thenReturn(taskDto1);
        // task1.notificationUsers is empty, so hasNotificationUser returns false

        // Act
        ProjectDto result = mapper.toDto(project, user, role);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getUserRole()).isEqualTo(UserRole.ADMIN);
        assertThat(result.getUsers()).containsExactly(userDto1);
        assertThat(result.getTasks()).hasSize(1);
        assertThat(result.getTasks().get(0).getId()).isEqualTo(4L);
        assertThat(result.getTasks().get(0).isNotified()).isFalse();
    }
}