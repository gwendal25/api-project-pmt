package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.task.ProjectTaskDto;
import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.TaskPriority;
import com.iscod.api_project_pmt.enums.TaskStatus;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.services.EmailService;
import com.iscod.api_project_pmt.services.ProjectUserService;
import com.iscod.api_project_pmt.services.TaskService;
import com.iscod.api_project_pmt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private ProjectUserService projectUserService;

    @Test
    void getTask_returns403_whenUserIsNull() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        when(userService.getUserById(10L)).thenReturn(null);

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTask_returns404_whenTaskNotFound() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        User user = new User();
        user.setId(10L);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(null);

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTask_returns403_whenUserNotInProject() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTask_returns200_andTaskDto_whenAccessGranted() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);
        project.setName("My Project");

        Task task = new Task();
        task.setId(taskId);
        task.setName("Task A");
        task.setDescription("Desc");
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        Date endDate = new Date();
        TaskDto dto = new TaskDto(
                taskId,
                "Task A",
                "Desc",
                TaskPriority.MEDIUM,
                TaskStatus.NOT_STARTED,
                endDate,
                List.of()
        );

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(taskService.getTaskDto(task)).thenReturn(dto);

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Task A"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.taskPriority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskStatus").value("NOT_STARTED"))
                .andExpect(jsonPath("$.taskHistoryEntries").isArray());
    }

    @Test
    void getTask_returns400_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", "not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskWithoutHistory_returns403_whenUserIsNull() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        when(userService.getUserById(10L)).thenReturn(null);

        mockMvc.perform(get("/tasks/{id}/no-history", taskId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTaskWithoutHistory_returns404_whenTaskNotFound() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        User user = new User();
        user.setId(10L);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(null);

        mockMvc.perform(get("/tasks/{id}/no-history", taskId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskWithoutHistory_returns403_whenUserNotInProject() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        mockMvc.perform(get("/tasks/{id}/no-history", taskId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTaskWithoutHistory_returns200_andSimpleTaskDto_whenAccessGranted() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setName("Task A");
        task.setDescription("Desc");
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        Date endDate = new Date();
        SimpleTaskDto dto = new SimpleTaskDto(
                taskId,
                "Task A",
                "Desc",
                TaskPriority.MEDIUM,
                TaskStatus.NOT_STARTED,
                endDate
        );

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(taskService.getSimpleTaskDto(task)).thenReturn(dto);

        mockMvc.perform(get("/tasks/{id}/no-history", taskId)
                        .header("Authorization", authorizationHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Task A"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.taskPriority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskStatus").value("NOT_STARTED"));
    }

    @Test
    void getTaskWithoutHistory_returns400_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;

        mockMvc.perform(get("/tasks/{id}/no-history", taskId)
                        .header("Authorization", "not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_returns400_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;

        String body = "{\"name\": \"Task Updated\",\"description\": \"Desc updated\", \"taskPriority\": \"MEDIUM\", \"taskStatus\": \"NOT_STARTED\", \"endDate\": \"2030-01-01 07:00:00\"} ";

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .header("Authorization", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_returns403_whenUserIsNull() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        String body = "{\"name\": \"Task Updated\",\"description\": \"Desc updated\", \"taskPriority\": \"MEDIUM\", \"taskStatus\": \"NOT_STARTED\", \"endDate\": \"2030-01-01 07:00:00\"} ";

        when(userService.getUserById(10L)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_returns404_whenTaskNotFound() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        String body = "{\"name\": \"Task Updated\",\"description\": \"Desc updated\", \"taskPriority\": \"MEDIUM\", \"taskStatus\": \"NOT_STARTED\", \"endDate\": \"2030-01-01 07:00:00\"} ";

        User user = new User();
        user.setId(10L);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_returns403_whenUserNotInProject() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        String body = "{\"name\": \"Task Updated\",\"description\": \"Desc updated\", \"taskPriority\": \"MEDIUM\", \"taskStatus\": \"NOT_STARTED\", \"endDate\": \"2030-01-01 07:00:00\"} ";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_returns403_whenRoleIsObserver() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        String body = "{\"name\": \"Task Updated\",\"description\": \"Desc updated\", \"taskPriority\": \"MEDIUM\", \"taskStatus\": \"NOT_STARTED\", \"endDate\": \"2030-01-01 07:00:00\"} ";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.OBSERVER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_returns200_andUpdatedSimpleTaskDto_whenAccessGranted() throws Exception {
        Long taskId = 42L;
        String authorizationHeader = "10";

        String body = "{\"name\": \"Task Updated\",\"description\": \"Desc updated\", \"taskPriority\": \"MEDIUM\", \"taskStatus\": \"NOT_STARTED\", \"endDate\": \"2030-01-01 07:00:00\"} ";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task originalTask = new Task();
        originalTask.setId(taskId);
        originalTask.setProject(project);

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName("Task Updated");
        updatedTask.setDescription("Desc updated");
        updatedTask.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        SimpleTaskDto updatedDto = new SimpleTaskDto(
                taskId,
                "Task Updated",
                "Desc updated",
                TaskPriority.MEDIUM,
                TaskStatus.NOT_STARTED,
                new Date()
        );

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(originalTask);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(taskService.addTaskHistoryEntry(any(), eq(originalTask))).thenReturn(updatedTask);
        when(taskService.getSimpleTaskDto(updatedTask)).thenReturn(updatedDto);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Task Updated"))
                .andExpect(jsonPath("$.description").value("Desc updated"))
                .andExpect(jsonPath("$.taskPriority").value("MEDIUM"))
                .andExpect(jsonPath("$.taskStatus").value("NOT_STARTED"));
    }

    @Test
    void assignTask_returns500_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignTask_returns403_whenUserIsNull() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        when(userService.getUserById(10L)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignTask_returns404_whenTaskNotFound() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignTask_returns403_whenUserNotInProject() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignTask_returns403_whenRoleIsObserver() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.OBSERVER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignTask_returns404_whenNewUserNotFound() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getUserById(20L)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignTask_returns401_whenNewUserNotInProject() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        User newUser = new User();
        newUser.setId(20L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getUserById(20L)).thenReturn(newUser);
        when(projectUserService.getByProjectAndUser(project, newUser)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void assignTask_returns403_whenNewUserAlreadyAssignedToTask() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        User oldUser = new User();
        oldUser.setId(20L);

        User newUser = new User();
        newUser.setId(20L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setUser(oldUser);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);
        ProjectUser newProjectUser = new ProjectUser(project, newUser, UserRole.MEMBER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getUserById(20L)).thenReturn(newUser);
        when(projectUserService.getByProjectAndUser(project, newUser)).thenReturn(newProjectUser);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignTask_returns200_andAssignsUser_whenOldUserIsNull() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        User newUser = new User();
        newUser.setId(20L);
        newUser.setName("New User");

        Project project = mock(Project.class);
        when(project.getName()).thenReturn("My Project");

        Task task = mock(Task.class);
        when(task.getProject()).thenReturn(project);
        when(task.getUser()).thenReturn(null);

        Task taskAfterAddUser = mock(Task.class);
        when(taskAfterAddUser.getProject()).thenReturn(project);
        when(taskAfterAddUser.getName()).thenReturn("Task A");
        when(taskAfterAddUser.getUser()).thenReturn(newUser);
        when(taskAfterAddUser.getUsersTaskAssignedNotifiedMails()).thenReturn(List.of("a@test.com"));

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.MEMBER);

        ProjectUser newProjectUser = new ProjectUser();
        newProjectUser.setRole(UserRole.MEMBER);

        ProjectTaskDto dto = new ProjectTaskDto();
        dto.setId(taskId);
        dto.setName("Task A");
        dto.setDescription("Desc");
        dto.setTaskPriority(TaskPriority.MEDIUM);
        dto.setTaskStatus(TaskStatus.NOT_STARTED);
        dto.setEndDate(new Date());

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getUserById(20L)).thenReturn(newUser);
        when(projectUserService.getByProjectAndUser(project, newUser)).thenReturn(newProjectUser);
        when(userService.assignTask(newUser, task)).thenReturn(newUser);
        when(taskService.addUser(task, newUser)).thenReturn(taskAfterAddUser);
        when(taskService.getProjectTaskDto(taskAfterAddUser, newUser)).thenReturn(dto);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Task A"));

        verify(emailService).SendTaskAssignNotificationBulk(
                eq(List.of("a@test.com")),
                eq("My Project"),
                eq("Task A"),
                eq("New User")
        );
        verify(taskService).getProjectTaskDto(taskAfterAddUser, newUser);
    }

    @Test
    void assignTask_returns200_andUnassignsOldUser_thenAssignsNewUser_whenOldUserExists() throws Exception {
        Long taskId = 42L;
        String body = "{\"userId\": 20}";

        User user = new User();
        user.setId(10L);

        User oldUser = new User();
        oldUser.setId(30L);

        User newUser = new User();
        newUser.setId(20L);
        newUser.setName("New User");

        Project project = mock(Project.class);
        when(project.getName()).thenReturn("My Project");

        Task task = mock(Task.class);
        when(task.getProject()).thenReturn(project);
        when(task.getUser()).thenReturn(oldUser);

        Task taskAfterAddUser = mock(Task.class);
        when(taskAfterAddUser.getProject()).thenReturn(project);
        when(taskAfterAddUser.getName()).thenReturn("Task A");
        when(taskAfterAddUser.getUser()).thenReturn(newUser);
        when(taskAfterAddUser.getUsersTaskAssignedNotifiedMails()).thenReturn(List.of("a@test.com"));

        ProjectUser projectUser = new ProjectUser();
        projectUser.setRole(UserRole.MEMBER);

        ProjectUser newProjectUser = new ProjectUser();
        newProjectUser.setRole(UserRole.MEMBER);

        ProjectTaskDto dto = new ProjectTaskDto();
        dto.setId(taskId);
        dto.setName("Task A");

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);
        when(userService.getUserById(20L)).thenReturn(newUser);
        when(projectUserService.getByProjectAndUser(project, newUser)).thenReturn(newProjectUser);
        when(userService.assignTask(newUser, task)).thenReturn(newUser);
        when(taskService.addUser(task, newUser)).thenReturn(taskAfterAddUser);
        when(taskService.getProjectTaskDto(taskAfterAddUser, newUser)).thenReturn(dto);

        mockMvc.perform(put("/tasks/{id}/assign", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Task A"));

        verify(emailService).SendTaskAssignNotificationBulk(
                eq(List.of("a@test.com")),
                eq("My Project"),
                eq("Task A"),
                eq("New User")
        );
        verify(taskService).getProjectTaskDto(taskAfterAddUser, newUser);
    }

    @Test
    void unassignTask_returns400_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unassignTask_returns403_whenUserIsNull() throws Exception {
        Long taskId = 42L;

        when(userService.getUserById(10L)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unassignTask_returns404_whenTaskNotFound() throws Exception {
        Long taskId = 42L;

        User user = new User();
        user.setId(10L);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unassignTask_returns403_whenUserNotInProject() throws Exception {
        Long taskId = 42L;

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unassignTask_returns403_whenRoleIsObserver() throws Exception {
        Long taskId = 42L;

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.OBSERVER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unassignTask_returns403_whenTaskNotAssignedToAnyUser() throws Exception {
        Long taskId = 42L;

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setUser(null);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unassignTask_returns200_andUnassignsUser_whenAccessGranted() throws Exception {
        Long taskId = 42L;

        User requester = new User();
        requester.setId(10L);

        User oldUser = new User();
        oldUser.setId(20L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        task.setUser(oldUser);

        ProjectUser projectUser = new ProjectUser(project, requester, UserRole.MEMBER);

        ProjectTaskDto dto = new ProjectTaskDto();
        dto.setId(taskId);
        dto.setName("Task A");

        when(userService.getUserById(10L)).thenReturn(requester);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, requester)).thenReturn(projectUser);
        when(taskService.getProjectTaskDto(task, null)).thenReturn(dto);

        mockMvc.perform(put("/tasks/{id}/unassign", taskId)
                        .header("Authorization", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Task A"));
    }

    @Test
    void setAssignNotifications_returns400_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;
        String body = "{\"isNotified\": true}";

        mockMvc.perform(put("/tasks/{id}/set-assign-notifications", taskId)
                        .header("Authorization", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void setAssignNotifications_returns403_whenUserIsNull() throws Exception {
        Long taskId = 42L;
        String body = "{\"isNotified\": true}";

        when(userService.getUserById(10L)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/set-assign-notifications", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void setAssignNotifications_returns404_whenTaskNotFound() throws Exception {
        Long taskId = 42L;
        String body = "{\"isNotified\": true}";

        User user = new User();
        user.setId(10L);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/set-assign-notifications", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void setAssignNotifications_returns403_whenUserNotInProject() throws Exception {
        Long taskId = 42L;
        String body = "{\"isNotified\": true}";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(null);

        mockMvc.perform(put("/tasks/{id}/set-assign-notifications", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void setAssignNotifications_returns200_andEnablesNotifications_whenIsNotifiedTrue() throws Exception {
        Long taskId = 42L;
        String body = "{\"isNotified\": true}";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        mockMvc.perform(put("/tasks/{id}/set-assign-notifications", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNotifications").value(true));
    }

    @Test
    void setAssignNotifications_returns200_andDisablesNotifications_whenIsNotifiedFalse() throws Exception {
        Long taskId = 42L;
        String body = "{\"isNotified\": false}";

        User user = new User();
        user.setId(10L);

        Project project = new Project();
        project.setId(99L);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.MEMBER);

        when(userService.getUserById(10L)).thenReturn(user);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(projectUserService.getByProjectAndUser(project, user)).thenReturn(projectUser);

        mockMvc.perform(put("/tasks/{id}/set-assign-notifications", taskId)
                        .header("Authorization", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNotifications").value(false));
    }
}