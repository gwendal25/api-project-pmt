package com.iscod.api_project_pmt.controllers;

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

        verify(taskService, never()).getTaskById(any());
        verify(projectUserService, never()).getByProjectAndUser(any(), any());
        verify(taskService, never()).getTaskDto(any());
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

        verify(projectUserService, never()).getByProjectAndUser(any(), any());
        verify(taskService, never()).getTaskDto(any());
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

        verify(taskService, never()).getTaskDto(any());
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

        verify(userService).getUserById(10L);
        verify(taskService).getTaskById(taskId);
        verify(projectUserService).getByProjectAndUser(project, user);
        verify(taskService).getTaskDto(task);
    }

    @Test
    void getTask_returns400_whenAuthorizationHeaderIsNotANumber() throws Exception {
        Long taskId = 42L;

        mockMvc.perform(get("/tasks/{id}", taskId)
                        .header("Authorization", "not-a-number"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(any());
        verify(taskService, never()).getTaskById(any());
        verify(projectUserService, never()).getByProjectAndUser(any(), any());
    }
}