package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.project.ProjectWithUserRolesDto;
import com.iscod.api_project_pmt.dtos.project.SimpleProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.*;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import com.iscod.api_project_pmt.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private SimpleProjectWithUserRolesMapper simpleProjectWithUserRolesMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectRepository projectRepository;

    @MockitoBean
    private ProjectUserRepository projectUserRepository;

    @MockitoBean
    private ProjectMapper projectMapper;

    @MockitoBean
    private ProjectUserMapper projectUserMapper;

    @MockitoBean
    private ProjectUserRoleMapper projectUserRoleMapper;

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private SimpleTaskMapper simpleTaskMapper;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private SimpleProjectMapper simpleProjectMapper;

    @Test
    public void testGetAllProjects_ReturnsEmptyList() throws Exception {
        // Arrange
        when(projectRepository.findAll()).thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetAllProjects_ReturnsListOfProjects() throws Exception {
        // Arrange
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        Date date2 = new Date();

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

        when(projectRepository.findAll()).thenReturn(Arrays.asList(project1, project2));
        when(simpleProjectMapper.toDto(project1)).thenReturn(dto1);
        when(simpleProjectMapper.toDto(project2)).thenReturn(dto2);

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

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(simpleProjectWithUserRolesMapper.toDtoList(user)).thenReturn(List.of(projectDto));

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
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        mockMvc.perform(get("/projects/all")
                        .header("Authorization", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}