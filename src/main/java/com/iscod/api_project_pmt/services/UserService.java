package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;

public interface UserService {
    User getUserById(Long id);

    User getByEmail(String email);

    User assignTask(User user, Task task);

    void unassignTask(User user, Task task);
}
