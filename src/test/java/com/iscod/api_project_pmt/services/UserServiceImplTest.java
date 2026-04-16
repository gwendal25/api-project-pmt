package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.UserMapper;
import com.iscod.api_project_pmt.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void testGetAllUsers_ReturnsEmptyList_WhenNoUsersExist() {
        // Arrange
        UserServiceImpl userService = new UserServiceImpl();
        userService.userRepository = userRepository;
        userService.userMapper = userMapper;

        List<User> listOf = List.of();
        when(userRepository.findAll()).thenReturn(listOf);

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllUsers_ReturnsMappedUsers_WhenUsersExist() {
        // Arrange
        UserServiceImpl userService = new UserServiceImpl();
        userService.userRepository = userRepository;
        userService.userMapper = userMapper;

        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        UserDto dto1 = new UserDto();
        dto1.setEmail("user1@example.com");

        UserDto dto2 = new UserDto();
        dto2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
    }
}
