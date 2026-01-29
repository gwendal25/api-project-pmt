package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.dtos.user.UserRequest;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    User getUserById(Long id);

    User getByEmail(String email);

    UserDto getDto(User user);

    User create(UserRequest userRequest);

    User assignTask(User user, Task task);

    void unassignTask(User user, Task task);

    void addNotificationTask(User user, Task task);

    void removeNotificationTask(User user, Task task);
}
