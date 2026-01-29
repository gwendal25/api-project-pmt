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
    void getUser_returns400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/users/{id}", "not-a-number"))
                .andExpect(status().isBadRequest());
    }


}
