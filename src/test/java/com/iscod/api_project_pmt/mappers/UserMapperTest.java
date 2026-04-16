package com.iscod.api_project_pmt.mappers;

import com.iscod.api_project_pmt.dtos.user.TaskUserDto;
import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.dtos.user.UserRequest;
import com.iscod.api_project_pmt.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapperImpl();
    }

    @Test
    void testToDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Act
        UserDto result = mapper.toDto(user);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testToUser() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setName("Jane Smith");
        request.setEmail("jane.smith@example.com");

        // Act
        User result = mapper.toUser(request);

        // Assert
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void testToTaskUserDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Alice Wonder");
        user.setEmail("alice.wonder@example.com");

        // Act
        TaskUserDto result = mapper.toTaskUserDto(user);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice Wonder");
    }

    @Test
    void testToTaskUserDtoList() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Bob Builder");
        user1.setEmail("bob.builder@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Charlie Chaplin");
        user2.setEmail("charlie.chaplin@example.com");

        List<User> users = List.of(user1, user2);

        // Act
        List<TaskUserDto> result = mapper.toTaskUserDtoList(users);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Bob Builder");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("Charlie Chaplin");
    }
}