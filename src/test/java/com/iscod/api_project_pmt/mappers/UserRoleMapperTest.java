package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.user.UserRoleDto;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRoleMapperTest {

    private UserRoleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserRoleMapperImpl();
    }

    @Test
    void testToUserRoleDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        ProjectUser projectUser = new ProjectUser();
        projectUser.setUser(user);
        projectUser.setRole(UserRole.ADMIN);

        // Act
        UserRoleDto result = mapper.toUserRoleDto(projectUser);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
    }
}