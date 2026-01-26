package com.iscod.api_project_pmt.services;

import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User assignTask(User user, Task task) {
        user.AssignTask(task);
        return userRepository.save(user);
    }

    @Override
    public void unassignTask(User user, Task task) {
        user.UnassignTask(task);
        userRepository.save(user);
    }

    @Override
    public void addNotificationTask(User user, Task task) {
        user.addNotificationTask(task);
        userRepository.save(user);
    }

    @Override
    public void removeNotificationTask(User user, Task task) {
        user.removeNotificationTask(task);
        userRepository.save(user);
    }
}
