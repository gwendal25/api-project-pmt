package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.project.ProjectUserRoleDto;
import com.iscod.api_project_pmt.dtos.user.UserRoleDto;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectUserRoleMapperTest {

    @Mock
    private UserRoleMapper userRoleMapper;

    private ProjectUserRoleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProjectUserRoleMapperImpl();
        mapper.userRoleMapper = userRoleMapper;
    }

    @Test
    void testToPartialUserRoleDto() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(new Date());

        // Act
        ProjectUserRoleDto result = mapper.toPartialUserRoleDto(project);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStartDate()).isEqualTo(project.getStartDate());
        // users should be null or empty as per mapping ignore
    }

    @Test
    void testToProjectUserRoleDto() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(new Date());

        // Mock users
        User user1 = new User();
        user1.setId(2L);
        user1.setName("User 1");
        user1.setEmail("user1@example.com");

        ProjectUser pu1 = new ProjectUser();
        pu1.setUser(user1);
        pu1.setRole(UserRole.ADMIN);

        project.setUsers(Set.of(pu1));

        UserRoleDto userRoleDto1 = new UserRoleDto();
        userRoleDto1.setId(2L);
        userRoleDto1.setName("User 1");
        userRoleDto1.setEmail("user1@example.com");
        userRoleDto1.setRole(UserRole.ADMIN);

        when(userRoleMapper.toUserRoleDto(pu1)).thenReturn(userRoleDto1);

        // Act
        ProjectUserRoleDto result = mapper.toProjectUserRoleDto(project);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStartDate()).isEqualTo(project.getStartDate());
        assertThat(result.getUsers()).hasSize(1);
        assertThat(result.getUsers().get(0)).isEqualTo(userRoleDto1);
    }
}