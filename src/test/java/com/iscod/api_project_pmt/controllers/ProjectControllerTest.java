package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.project.*;
import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserDto;
import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.mappers.*;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import com.iscod.api_project_pmt.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    EmailService emailService;
    @MockitoBean
    ProjectService projectService;
    @MockitoBean
    ProjectUserService projectUserService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    TaskService taskService;

    @Test
    public void testGetAllProjects_ReturnsEmptyList() throws Exception {
        // Arrange
        when(projectService.getAllProjects()).thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetProject_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        ProjectDto projectDto = new ProjectDto();

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(projectService.getProjectDto(project, user, projectUser)).thenReturn(projectDto);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProject_NotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProject_Forbidden() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetProject_Unauthorized() throws Exception {
        // Arrange
        Long userId = 999L;
        Long projectId = 123L;

        when(userService.getUserById(userId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAllProjects_ReturnsListOfProjects() throws Exception {
        // Arrange
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        project1.setStartDate(new Date());

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setStartDate(new Date());

        SimpleProjectDto dto1 = new SimpleProjectDto();
        dto1.setStartDate(project1.getStartDate());

        SimpleProjectDto dto2 = new SimpleProjectDto();
        dto2.setStartDate(project2.getStartDate());

        when(projectService.getAllProjects()).thenReturn(Arrays.asList(dto1, dto2));

        // Act and Assert
        mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].startDate").value(dateFormat.format(dto1.getStartDate())))
                .andExpect(jsonPath("$[1].startDate").value(dateFormat.format(dto2.getStartDate())));
    }

    @Test
    public void testGetAllProjectsByUser_ReturnsProjectsForValidUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ProjectWithUserRolesDto projectDto = new ProjectWithUserRolesDto();
        projectDto.setId(1L);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getAllProjectsByUser(user)).thenReturn(List.of(projectDto));

        // Act and Assert
        mockMvc.perform(get("/projects/all")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(projectDto.getId()));
    }

    @Test
    public void testGetAllProjectsByUser_ThrowsForbiddenForInvalidUser() throws Exception {
        // Arrange
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/all")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetProjectWithUserRoles_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        ProjectUserRoleDto projectUserRoleDto = new ProjectUserRoleDto();

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(projectUserService.getProjectUserRoleDto(project)).thenReturn(projectUserRoleDto);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}/user-roles", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProjectWithUserRoles_UserNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;

        when(userService.getUserById(userId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}/user-roles", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetProjectWithUserRoles_ProjectNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}/user-roles", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProjectWithUserRoles_AccessDenied() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 123L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(get("/projects/{id}/user-roles", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateProject_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ProjectRequest request = new ProjectRequest();
        request.setName("New Project");

        Project project = new Project();
        project.setId(100L);
        project.setName("New Project");

        SimpleProjectDto simpleDto = new SimpleProjectDto();
        simpleDto.setId(100L);
        simpleDto.setName("New Project");

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.saveProject(any(ProjectRequest.class))).thenReturn(project);
        when(projectService.getSimpleProjectDto(project)).thenReturn(simpleDto);

        String jsonRequest = "{\"name\":\"New Project\"}";

        // Act and Assert
        mockMvc.perform(post("/projects")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/projects/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("New Project"));

        verify(projectUserService).save(eq(project), eq(user), any());
    }

    @Test
    public void testCreateProject_UserNotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);

        String jsonRequest = "{\"name\":\"New Project\"}";

        // Act and Assert
        mockMvc.perform(post("/projects")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateTask_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.ADMIN);

        Task task = new Task();
        task.setId(50L);
        task.setName("New Task");

        SimpleTaskDto taskDto = new SimpleTaskDto();
        taskDto.setId(50L);
        taskDto.setName("New Task");

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(taskService.save(any(TaskRequest.class), eq(project))).thenReturn(task);
        when(taskService.getSimpleTaskDto(task)).thenReturn(taskDto);

        String jsonRequest = "{\"name\":\"New Task\"}";

        // Act and Assert
        mockMvc.perform(post("/projects/{id}/tasks", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tasks/50"))
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.name").value("New Task"));

        verify(projectService).addTask(project, task);
    }

    @Test
    public void testCreateTask_UserNotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        Long projectId = 100L;
        when(userService.getUserById(userId)).thenReturn(null);

        String jsonRequest = "{\"name\":\"New Task\"}";

        // Act and Assert
        mockMvc.perform(post("/projects/{id}/tasks", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateTask_ProjectNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 999L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        String jsonRequest = "{\"name\":\"New Task\"}";

        // Act and Assert
        mockMvc.perform(post("/projects/{id}/tasks", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateTask_AccessDenied() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        String jsonRequest = "{\"name\":\"New Task\"}";

        // Act and Assert
        mockMvc.perform(post("/projects/{id}/tasks", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateTask_ObserverCannotCreate() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.OBSERVER);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        String jsonRequest = "{\"name\":\"New Task\"}";

        // Act and Assert
        mockMvc.perform(post("/projects/{id}/tasks", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateProject_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.ADMIN);

        SimpleProjectDto updatedDto = new SimpleProjectDto();
        updatedDto.setId(projectId);
        updatedDto.setName("Updated Name");

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(projectService.getSimpleProjectDto(project)).thenReturn(updatedDto);

        String jsonRequest = "{\"name\":\"Updated Name\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(projectService).updateProject(any(ProjectRequest.class), eq(project));
    }

    @Test
    public void testUpdateProject_UserNotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        Long projectId = 100L;
        when(userService.getUserById(userId)).thenReturn(null);

        String jsonRequest = "{\"name\":\"Updated Name\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateProject_ProjectNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 999L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        String jsonRequest = "{\"name\":\"Updated Name\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProject_AccessDenied() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        String jsonRequest = "{\"name\":\"Updated Name\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateProject_ObserverCannotUpdate() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.OBSERVER);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        String jsonRequest = "{\"name\":\"Updated Name\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddUserToProject_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        String newUserEmail = "newuser@example.com";
        User user = new User();
        user.setId(userId);
        user.setName("Admin User");

        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.ADMIN);

        User newUser = new User();
        newUser.setEmail(newUserEmail);

        ProjectUser newProjectUser = new ProjectUser();
        ProjectUserDto resultDto = new ProjectUserDto();

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getByEmail(newUserEmail)).thenReturn(newUser);
        when(projectUserService.save(project, newUser, UserRole.MEMBER)).thenReturn(newProjectUser);
        when(projectUserService.getProjectUserDto(newProjectUser)).thenReturn(resultDto);

        String jsonRequest = "{\"email\":\"" + newUserEmail + "\", \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/add-user", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(emailService).SendProjectInvite(eq("Admin User"), eq(newUserEmail), eq("Test Project"), eq("MEMBER"));
    }

    @Test
    public void testAddUserToProject_UserNotLoggedIn() throws Exception {
        // Arrange
        Long userId = 999L;
        Long projectId = 100L;
        when(userService.getUserById(userId)).thenReturn(null);

        String jsonRequest = "{\"email\":\"new@test.com\", \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/add-user", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddUserToProject_ProjectNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 999L;
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        String jsonRequest = "{\"email\":\"new@test.com\", \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/add-user", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUserToProject_AccessDenied() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        String jsonRequest = "{\"email\":\"new@test.com\", \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/add-user", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddUserToProject_Forbidden_InsufficientRole() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.MEMBER); // Member cannot add users

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        String jsonRequest = "{\"email\":\"new@test.com\", \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/add-user", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAddUserToProject_NewUserNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 100L;
        User user = new User();
        user.setId(userId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.ADMIN);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getByEmail("nonexistent@test.com")).thenReturn(null);

        String jsonRequest = "{\"email\":\"nonexistent@test.com\", \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/add-user", projectId)
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testChangeUserRole_Success() throws Exception {
        // Arrange
        Long requesterId = 1L;
        Long projectId = 100L;
        Long targetUserId = 2L;
        User requester = new User();
        requester.setId(requesterId);

        Project project = new Project();
        project.setId(projectId);

        ProjectUser requesterProjectUser = new ProjectUser();
        requesterProjectUser.setRole(UserRole.ADMIN);

        User targetUser = new User();
        targetUser.setId(targetUserId);

        ProjectUser targetProjectUser = new ProjectUser();
        ProjectUserDto resultDto = new ProjectUserDto();

        when(userService.getUserById(requesterId)).thenReturn(requester);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, requester)).thenReturn(requesterProjectUser);
        when(userService.getUserById(targetUserId)).thenReturn(targetUser);
        when(projectUserService.getByProjectAndUser(project, targetUser)).thenReturn(targetProjectUser);
        when(projectUserService.getProjectUserDto(targetProjectUser)).thenReturn(resultDto);

        String jsonRequest = "{\"userId\":2, \"userRole\":\"MEMBER\"}";

        // Act and Assert
        mockMvc.perform(put("/projects/{id}/change-user-role", projectId)
                        .header("Authorization", requesterId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(projectUserService).updateUserRole(targetProjectUser, UserRole.MEMBER);
    }

    @Test
    public void testChangeUserRole_UserNotLoggedIn() throws Exception {
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);
        String jsonRequest = "{\"userId\":2, \"userRole\":\"MEMBER\"}";

        mockMvc.perform(put("/projects/100/change-user-role")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeUserRole_ProjectNotFound() throws Exception {
        Long userId = 1L;
        User user = new User();
        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(999L)).thenReturn(null);
        String jsonRequest = "{\"userId\":2, \"userRole\":\"MEMBER\"}";

        mockMvc.perform(put("/projects/999/change-user-role")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testChangeUserRole_RequesterNotInProject() throws Exception {
        Long userId = 1L;
        User user = new User();
        Project project = new Project();
        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(100L)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);
        String jsonRequest = "{\"userId\":2, \"userRole\":\"MEMBER\"}";

        mockMvc.perform(put("/projects/100/change-user-role")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeUserRole_Forbidden_InsufficientRole() throws Exception {
        Long userId = 1L;
        User user = new User();
        Project project = new Project();
        ProjectUser pu = new ProjectUser();
        pu.setRole(UserRole.MEMBER);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(100L)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(pu);
        String jsonRequest = "{\"userId\":2, \"userRole\":\"ADMIN\"}";

        mockMvc.perform(put("/projects/100/change-user-role")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeUserRole_TargetUserNotFound() throws Exception {
        Long userId = 1L;
        User user = new User();
        Project project = new Project();
        ProjectUser pu = new ProjectUser();
        pu.setRole(UserRole.ADMIN);

        when(userService.getUserById(userId)).thenReturn(user);
        when(projectService.getProjectById(100L)).thenReturn(project);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(pu);
        when(userService.getUserById(2L)).thenReturn(null);
        String jsonRequest = "{\"userId\":2, \"userRole\":\"MEMBER\"}";

        mockMvc.perform(put("/projects/100/change-user-role")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }
}