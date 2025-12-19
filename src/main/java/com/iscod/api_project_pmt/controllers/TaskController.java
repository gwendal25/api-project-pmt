package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.*;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.mappers.SimpleTaskMapper;
import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import com.iscod.api_project_pmt.services.TaskHistoryEntryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks")
public class TaskController {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final SimpleTaskMapper simpleTaskMapper;
    private final ProjectMapper projectMapper;
    private final TaskHistoryEntryService taskHistoryEntryService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskMapper.toDtoWithHistory(task));
    }

    @GetMapping("/{id}/no-history")
    public ResponseEntity<SimpleTaskDto> getTaskWithoutHistory(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(simpleTaskMapper.toDto(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimpleTaskDto> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }

        taskHistoryEntryService.AddTaskHistoryEntryToTask(taskRequest, task);
        return ResponseEntity.ok(simpleTaskMapper.toDto(task));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ProjectTaskDto> assignTask(@PathVariable Long id, @RequestBody TaskUserRequest taskUserRequest) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }

        User oldUser = task.getUser();
        if(oldUser != null) {
            oldUser.UnassignTask(task);
            userRepository.save(oldUser);
        }
        Long userId = taskUserRequest.getUserId();
        if(userId == -1) {
            return ResponseEntity.ok(projectMapper.toProjectTaskDto(task, new User(-1L, "")));
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        user.AssignTask(task);
        user = userRepository.save(user);
        task.setUser(user);
        task = taskRepository.save(task);

        return ResponseEntity.ok(projectMapper.toProjectTaskDto(task, user));
    }

    @PutMapping("/{id}/set-assign-notifications")
    public ResponseEntity<TaskSetNotificationDto> setAssignNotifications(@PathVariable Long id, @RequestBody TaskNotificationRequest taskNotificationRequest, @RequestHeader("Authorization") String userIdStr) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        if(taskNotificationRequest.getIsNotified()){
            task.addNotificationUser(user);
            user.addNotificationTask(task);
        }
        else {
            task.removeNotificationUser(user);
            user.removeNotificationTask(task);
        }
        taskRepository.save(task);
        userRepository.save(user);

        return ResponseEntity.ok(new TaskSetNotificationDto(taskNotificationRequest.getIsNotified()));
    }
}
