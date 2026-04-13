package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.project.SimpleProjectDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.mappers.SimpleProjectMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SimpleProjectMapper simpleProjectMapper;

    @Test
    void testGetAllProjects_ReturnsEmptyList_WhenNoProjectsExist() {
        // Arrange
        ProjectServiceImpl projectService = new ProjectServiceImpl();
        projectService.projectRepository = projectRepository;
        projectService.simpleProjectMapper = simpleProjectMapper;

        List<Project> listOf = List.of();
        when(projectRepository.findAll()).thenReturn(listOf);

        // Act
        List<SimpleProjectDto> result = projectService.getAllProjects();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllProjects_ReturnsMappedProjects_WhenProjectsExist() {
        // Arrange
        ProjectServiceImpl projectService = new ProjectServiceImpl();
        projectService.projectRepository = projectRepository;
        projectService.simpleProjectMapper = simpleProjectMapper;

        Project project1 = new Project();
        project1.setName("Project 1");
        project1.setStartDate(new Date());

        Project project2 = new Project();
        project2.setName("Project 2");
        project2.setStartDate(new Date());

        SimpleProjectDto dto1 = new SimpleProjectDto();
        dto1.setStartDate(project1.getStartDate());

        SimpleProjectDto dto2 = new SimpleProjectDto();
        dto2.setStartDate(project2.getStartDate());

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(simpleProjectMapper.toDto(project1)).thenReturn(dto1);
        when(simpleProjectMapper.toDto(project2)).thenReturn(dto2);

        // Act
        List<SimpleProjectDto> result = projectService.getAllProjects();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
    }
}