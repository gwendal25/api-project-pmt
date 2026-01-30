package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.task.*;
import com.iscod.api_project_pmt.dtos.user.TaskUserRequest;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.services.*;
import jakarta.validation.Valid;
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
    private final EmailService emailService;
    private final UserService userService;
    private final TaskService taskService;
    private final ProjectUserService projectUserService;

    /**
     * Récupère les données complètes d'une tâche avec son historique et les renvoie
     * @param id L'id de la tâche à récupérer
     * @param userIdStr Un faux token d'accès (id de l'utilisateur)
     * @return Les données de la tâche (id, nom, description, priorité, status, date de fin et historique des modifications
     * <strong>Exemple de réponse :</strong>
     * <pre>{@code
     * {
     *     "id": 1,
     *     "name": "Ajout du controller de base",
     *     "description": "Ajouter un character controller basique pour marcher, courir et sauter en 3D",
     *     "taskPriority": "HIGH",
     *     "taskStatus": "IN_PROGRESS",
     *     "endDate": "2021-03-09",
     *     "taskHistoryEntries": [
     *          "id": 1,
     *          "name": "Ajout du controller de base",
     *          "description": "Ajouter un character controller basique en 3D",
     *          "taskPriority": "HIGH",
     *          "taskStatus": "NOT_STARTED",
     *          "endDate": "2021-03-09"
     *          "editDate": "2021-03-09 9:15:00"
     *     ]
     * }
     * }</pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id, @Valid @RequestHeader("Authorization") String userIdStr) {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The token you provided is not a valid number");
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskService.getTaskById(id);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(task.getProject(), user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(taskService.getTaskDto(task));
    }

    /**
     * Cette méthode renvoie les données d'une tâche sans les entrées d'historique associées
     * @param id L'id de la tâche à récupérer
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @return les données de la tâche avec id, nom, description, priorité, status et date de fin
     */
    @GetMapping("/{id}/no-history")
    public ResponseEntity<SimpleTaskDto> getTaskWithoutHistory(@PathVariable Long id, @Valid @RequestHeader("Authorization") String userIdStr) {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The token you provided is not a valid number");
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskService.getTaskById(id);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(task.getProject(), user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(taskService.getSimpleTaskDto(task));
    }

    /**
     * Cette méthode met à jour les informations d'une tâche
     * @param id L'id de la tâche à mettre à jour
     * @param taskRequest les données de mise à jour de la tâche avec nom, description, priorité, status et date de fin
     * @param userIdStr Un faux token d'authorisation qui est l'id de l'utilisateur
     * @return Les données de la tâche mise à jour avec id, nom, description, priorité, status et date de fin
     */
    @PutMapping("/{id}")
    public ResponseEntity<SimpleTaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest, @RequestHeader("Authorization") String userIdStr) {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The token you provided is not a valid number");
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskService.getTaskById(id);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(task.getProject(), user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot update tasks");
        }

        task = taskService.addTaskHistoryEntry(taskRequest, task);
        return ResponseEntity.ok(taskService.getSimpleTaskDto(task));
    }

    /**
     * Cette méthode assigne une tâche à un utilisateur
     * @param id l'id de la tâche à assigner
     * @param taskUserRequest Un objet qui contient l'id de l'user a assigné à la tâche
     * @param userIdStr Un faux token d'authorisation qui est l'id de l'utilisateur
     * @return Les données de la tâche avec l'utilisateur assigné
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<ProjectTaskDto> assignTask(@PathVariable Long id, @RequestBody TaskUserRequest taskUserRequest, @RequestHeader("Authorization") String userIdStr) {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The token you provided is not a valid number");
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskService.getTaskById(id);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(task.getProject(), user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot assign tasks");
        }

        Long newUserId = taskUserRequest.getUserId();
        User newUser = userService.getUserById(newUserId);
        if(newUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : The user you are trying to assign the task to could not be found");
        }

        ProjectUser newProjectUser = projectUserService.getByProjectAndUser(task.getProject(), newUser);
        if(newProjectUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied : The user isn't part of this projet");
        }

        User oldUser = task.getUser();
        if(oldUser != null && oldUser.getId().equals(newUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect request : The user you are trying to assign the task to is already assigned to the task");
        }

        if(oldUser != null) {
            userService.unassignTask(oldUser, task);
        }
        newUser = userService.assignTask(newUser, task);
        task = taskService.addUser(task, newUser);

        emailService.SendTaskAssignNotificationBulk(task.getUsersTaskAssignedNotifiedMails(),
                task.getProject().getName(),
                task.getName(),
                task.getUser().getName());
        return ResponseEntity.ok(taskService.getProjectTaskDto(task, newUser));
    }

    @PutMapping("/{id}/unassign")
    public ResponseEntity<ProjectTaskDto> unassignTask(@PathVariable Long id, @RequestHeader("Authorization") String userIdStr) {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The token you provided is not a valid number");
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskService.getTaskById(id);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(task.getProject(), user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot assign tasks");
        }

        User oldUser = task.getUser();
        if(oldUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect request : The task is not assigned to any user");
        }

        userService.unassignTask(oldUser, task);
        taskService.removeUser(task);
        return ResponseEntity.ok(taskService.getProjectTaskDto(task, null));
    }

    /**
     * Cette méthode active les notifications par mail lorsque la tâche est assigné à un nouvel utilisateur
     * @param id l'id de la tâche a assigné
     * @param taskNotificationRequest Un objet qui contient le statut des notifications par mail d'assignation de tâches
     * @param userIdStr Un faux token d'authorisation qui est l'id de l'utilisateur
     * @return Le statut de notification par mail lors de l'assignation de la tâche
     */
    @PutMapping("/{id}/set-assign-notifications")
    public ResponseEntity<TaskSetNotificationDto> setAssignNotifications(@PathVariable Long id, @RequestBody TaskNotificationRequest taskNotificationRequest, @RequestHeader("Authorization") String userIdStr) {
        Long userId;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The token you provided is not a valid number");
        }
        User user = userService.getUserById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Task task = taskService.getTaskById(id);
        if(task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found : This is not the task you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(task.getProject(), user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(taskNotificationRequest.getIsNotified()){
            taskService.addNotificationUser(task, user);
            userService.addNotificationTask(user, task);
        }
        else {
            taskService.removeNotificationUser(task, user);
            userService.removeNotificationTask(user, task);
        }

        return ResponseEntity.ok(new TaskSetNotificationDto(taskNotificationRequest.getIsNotified()));
    }
}
