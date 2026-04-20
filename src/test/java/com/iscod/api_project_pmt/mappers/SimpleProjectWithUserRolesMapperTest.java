package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectWithUserRolesDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleProjectWithUserRolesMapperTest {

    private SimpleProjectWithUserRolesMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SimpleProjectWithUserRolesMapperImpl();
    }

    @Test
    void testToDto() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(new Date());

        User user = new User();
        user.setId(1L);

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setRole(UserRole.ADMIN);

        // Act
        ProjectWithUserRolesDto result = mapper.toDto(projectUser);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStartDate()).isEqualTo(project.getStartDate());
        assertThat(result.getUserRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void testToDtoList() {
        // Arrange
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

        User user = new User();
        user.setId(1L);

        ProjectUser pu1 = new ProjectUser();
        pu1.setProject(project1);
        pu1.setUser(user);
        pu1.setRole(UserRole.ADMIN);

        ProjectUser pu2 = new ProjectUser();
        pu2.setProject(project2);
        pu2.setUser(user);
        pu2.setRole(UserRole.MEMBER);

        user.setProjects(Set.of(pu1, pu2));

        // Act
        List<ProjectWithUserRolesDto> result = mapper.toDtoList(user);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(ProjectWithUserRolesDto::getId)).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.stream().map(ProjectWithUserRolesDto::getName)).containsExactlyInAnyOrder("Project 1", "Project 2");
        assertThat(result.stream().map(ProjectWithUserRolesDto::getUserRole)).containsExactlyInAnyOrder(UserRole.ADMIN, UserRole.MEMBER);
    }
}