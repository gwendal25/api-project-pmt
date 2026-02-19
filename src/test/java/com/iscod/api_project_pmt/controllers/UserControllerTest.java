package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.entities.User;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getUser_returns404_whenUserNotFound() throws Exception {
        Long userId = 42L;

        when(userService.getUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_returns200_whenUserFound() throws Exception {
        Long userId = 42L;

        User user = new User();
        user.setId(userId);

        UserDto dto = mock(UserDto.class);

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.getDto(user)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void createUser_returns400_whenPasswordsDoNotMatch() throws Exception {
        String body = """
            {
              "name": "Alice",
              "email": "alice@test.com",
              "password": "pwd-1",
              "repeatPassword": "pwd-2"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_returns400_whenEmailAlreadyUsed() throws Exception {
        String body = """
            {
              "name": "Alice",
              "email": "alice@test.com",
              "password": "pwd",
              "repeatPassword": "pwd"
            }
            """;

        when(userService.getByEmail("alice@test.com")).thenReturn(new User());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_returns201_withLocationHeader_whenCreated() throws Exception {
        String body = """
            {
              "name": "Alice",
              "email": "alice@test.com",
              "password": "pwd",
              "repeatPassword": "pwd"
            }
            """;

        User created = new User();
        created.setId(123L);

        UserDto dto = mock(UserDto.class);

        when(userService.getByEmail("alice@test.com")).thenReturn(null);
        when(userService.create(any())).thenReturn(created);
        when(userService.getDto(created)).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/123"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void login_returns404_whenUserNotFound() throws Exception {
        String body = """
            {
              "email": "missing@test.com",
              "password": "pwd"
            }
            """;

        when(userService.getByEmail("missing@test.com")).thenReturn(null);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_returns400_whenPasswordIsIncorrect() throws Exception {
        String body = """
            {
              "email": "user@test.com",
              "password": "wrong"
            }
            """;

        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("correct");

        when(userService.getByEmail("user@test.com")).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returns200_whenCredentialsAreCorrect() throws Exception {
        String body = """
            {
              "email": "user@test.com",
              "password": "correct"
            }
            """;

        User user = new User();
        user.setId(10L);
        user.setEmail("user@test.com");
        user.setPassword("correct");

        UserDto dto = mock(UserDto.class);

        when(userService.getByEmail("user@test.com")).thenReturn(user);
        when(userService.getDto(user)).thenReturn(dto);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
