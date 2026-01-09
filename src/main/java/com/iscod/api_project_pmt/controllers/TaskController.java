package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.task.*;
import com.iscod.api_project_pmt.dtos.user.TaskUserRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.mappers.ProjectMapper;
import com.iscod.api_project_pmt.mappers.SimpleTaskMapper;
import com.iscod.api_project_pmt.mappers.TaskMapper;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import com.iscod.api_project_pmt.services.TaskHistoryEntryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks")
public class TaskController {
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final SimpleTaskMapper simpleTaskMapper;
    private final ProjectMapper projectMapper;
    private final TaskHistoryEntryService taskHistoryEntryService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id, @RequestHeader("Authorization") String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        Project project = task.getProject();
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(taskMapper.toDtoWithHistory(task));
    }

    @GetMapping("/{id}/no-history")
    public ResponseEntity<SimpleTaskDto> getTaskWithoutHistory(@PathVariable Long id, @RequestHeader("Authorization") String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        Project project = task.getProject();
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(simpleTaskMapper.toDto(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimpleTaskDto> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest, @RequestHeader("Authorization") String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        Project project = task.getProject();
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        UserRole role = projectUser.getRole();
        if(role == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot update tasks");
        }

        taskHistoryEntryService.AddTaskHistoryEntryToTask(taskRequest, task);
        return ResponseEntity.ok(simpleTaskMapper.toDto(task));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ProjectTaskDto> assignTask(@PathVariable Long id, @RequestBody TaskUserRequest taskUserRequest, @RequestHeader("Authorization") String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        Project project = task.getProject();
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        UserRole role = projectUser.getRole();
        if(role == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot assign tasks");
        }

        User oldUser = task.getUser();
        if(oldUser != null) {
            oldUser.UnassignTask(task);
            userRepository.save(oldUser);
        }
        Long newUserId = taskUserRequest.getUserId();
        if(newUserId == -1) {
            if(oldUser != null) {
                task.setUser(null);
                task = taskRepository.save(task);
            }
            return ResponseEntity.ok(projectMapper.toProjectTaskDto(task, new User(-1L, "")));
        }
        User newUser = userRepository.findById(newUserId).orElse(null);
        if(newUser == null) {
            if(oldUser != null) {
                task.setUser(null);
                task = taskRepository.save(task);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : The user you are trying to assign the task to could not be found");
        }

        ProjectUser newProjectUser = projectUserRepository.findByProjectAndUser(project, newUser).orElse(null);
        if(newProjectUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied : The user isn't part of this projet");
        }

        newUser.AssignTask(task);
        newUser = userRepository.save(newUser);
        task.setUser(newUser);
        task = taskRepository.save(task);

        return ResponseEntity.ok(projectMapper.toProjectTaskDto(task, newUser));
    }

    @PutMapping("/{id}/set-assign-notifications")
    public ResponseEntity<TaskSetNotificationDto> setAssignNotifications(@PathVariable Long id, @RequestBody TaskNotificationRequest taskNotificationRequest, @RequestHeader("Authorization") String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskRepository.findById(id).orElse(null);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        Project project = task.getProject();
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
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
