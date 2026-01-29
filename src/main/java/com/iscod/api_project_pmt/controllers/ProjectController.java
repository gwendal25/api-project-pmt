package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.project.*;
import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserDto;
import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserIdRequest;
import com.iscod.api_project_pmt.dtos.projectuser.ProjectUserRequest;
import com.iscod.api_project_pmt.dtos.task.SimpleTaskDto;
import com.iscod.api_project_pmt.dtos.task.TaskRequest;
import com.iscod.api_project_pmt.entities.Project;
import com.iscod.api_project_pmt.entities.ProjectUser;
import com.iscod.api_project_pmt.entities.Task;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.enums.UserRole;
import com.iscod.api_project_pmt.services.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/projects")
public class ProjectController {
    private final EmailService emailService;
    private final ProjectService projectService;
    private final ProjectUserService projectUserService;
    private final UserService userService;
    private final TaskService taskService;

    /**
     * Cette méthode renvoie la liste de tous les projets dans la base de données (debug only).
     * @return une liste de projets simplifiés avec id, nom, description et date de début de chaque projet
     * <strong>Exemple de réponse :</strong>
     * <pre><code>
     *     [
     *       {
     *         "id": 1,
     *         "name": "Projet de factory",
     *         "description": "Projet de factory visant à automatiser le processus de mise en culture des paras dans la factory 76",
     *         "startDate": "2021-03-01"
     *       }
     *     ]
     * </code></pre>
     */
    @GetMapping
    public List<SimpleProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    /**
     * Cette méthode renvoie la liste de tous les projets auquel l'utilisateur à accès
     * @param userIdStr Un faux token d'authorisation qui est l'id de l'utilisateur qui crée le projet
     * @return une liste de projets simplifiés avec id, nom, description et date de début de chaque projet
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProjectWithUserRolesDto>> getAllProjectsByUser(@RequestHeader("Authorization") String userIdStr) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied : You need to log in in order to access your list of projects");
        }

        return ResponseEntity.ok(projectService.getAllProjectsByUser(user));
    }

    /**
     * Cette méthode permet de récupérer les informations d'un projet via son id
     * Le projet contient id, nom, description, date de début, liste des tâches et liste des utilisateurs.
     * @param id Id du projet
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @return Les informations du projet
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id, @RequestHeader("Authorization") String userIdStr) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.getProjectById(id);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(project, user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(projectService.getProjectDto(project, user, projectUser));
    }

    /**
     * Récupère les informations minimales d'un projet ainsi que les utilisateurs associés et leurs rôles
     * Le projet contient id, nom, description, date de début et liste des utilisateurs
     * @param id Id du projet
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @return les infos du projet avec les rôles utilisateurs
     */
    @GetMapping("/{id}/user-roles")
    public ResponseEntity<ProjectUserRoleDto> getProjectWithUserRoles(@PathVariable Long id, @RequestHeader("Authorization") String userIdStr) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.getProjectById(id);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(project, user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(projectUserService.getProjectUserRoleDto(project));
    }

    /**
     * Creates a new project and associates it with the authenticated user.
     * Throws a 403 Forbidden exception if the user is not logged in.
     *
     * @param projectRequest The details of the project to be created, including name, description, and start date.
     * @param userIdStr The user's ID passed as an Authorization header.
     * @param uriBuilder The URI builder used to construct the location of the newly created project.
     * @return A ResponseEntity containing the simplified project details (id, name, description, and start date) along with the appropriate HTTP status.
     */
    @PostMapping
    public ResponseEntity<SimpleProjectDto> CreateProject(@RequestBody ProjectRequest projectRequest, @RequestHeader("Authorization") String userIdStr, UriComponentsBuilder uriBuilder) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.saveProject(projectRequest);
        projectUserService.save(project, user, UserRole.ADMIN);

        return ResponseEntity.created(uriBuilder
                .path("/projects/{id}")
                .buildAndExpand(project.getId())
                .toUri())
                .body(projectService.getSimpleProjectDto(project));
    }

    /**
     * Créer une nouvelle tâche et l'associe à un projet
     * Les infos de la tâche sont nom, description, priorité, status et date de fin
     * @param id Id du projet auquel ajouter une nouvelle tâche
     * @param taskRequest Les informations pour créer la tâche
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @param uriBuilder Le builder de l'url de la tâche
     * @return Les infos de la tâche avec id, nom, description, priorité, status et date de fin
     */
    @PostMapping("/{id}/tasks")
    public ResponseEntity<SimpleTaskDto> CreateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest, @RequestHeader("Authorization") String userIdStr, UriComponentsBuilder uriBuilder) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.getProjectById(id);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(project, user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot create tasks");
        }

        Task task = taskService.save(taskRequest, project);
        projectService.addTask(project, task);
        return ResponseEntity.created(uriBuilder
                .path("/tasks/{id}")
                .buildAndExpand(task.getId())
                .toUri())
                .body(taskService.getSimpleTaskDto(task));
    }

    /**
     * Met à jour les informations du projet
     * Les infos du projet sont nom, description et date de début
     * @param id Id du projet à modifier
     * @param projectRequest Les informations de mise à jour du projet
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @return Les informations modifiées du projet avec id, nom, description et date de début
     */
    @PutMapping("/{id}")
    public ResponseEntity<SimpleProjectDto> UpdateProject(@PathVariable Long id, @RequestBody ProjectRequest projectRequest, @RequestHeader("Authorization") String userIdStr) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.getProjectById(id);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(project, user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot create tasks");
        }

        projectService.updateProject(projectRequest, project);
        return ResponseEntity.ok(projectService.getSimpleProjectDto(project));
    }

    /**
     * Ajoute un utilisateur à un projet en lui donnant un rôle sur ce projet
     * @param id Id du projet auquel associé un nouvel utilisateur
     * @param projectUserRequest les informations de l'utilisateur à ajouter au projet avec son email et son rôle sur le projet
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @return Une version simplifiée de la relation entre l'utilisateur et le projet
     */
    @PutMapping("/{id}/add-user")
    public ResponseEntity<ProjectUserDto> AddUserToProject(@PathVariable Long id, @RequestBody ProjectUserRequest projectUserRequest, @RequestHeader("Authorization") String userIdStr){
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.getProjectById(id);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(project, user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER || projectUser.getRole() == UserRole.MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer or member cannot add users to a project");
        }

        User newUser = userService.getByEmail(projectUserRequest.getEmail());
        if(newUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : The user you are trying to invite could not be found");
        }

        ProjectUser newProjectUser = projectUserService.save(project, newUser, projectUserRequest.getUserRole());
        emailService.SendProjectInvite(user.getName(), newUser.getEmail(), project.getName(), projectUserRequest.getUserRole().toString());
        return ResponseEntity.ok(projectUserService.getProjectUserDto(newProjectUser));
    }

    /**
     * Change le rôle de l'utilisateur associé à un projet
     * @param id Id du projet
     * @param projectUserIdRequest Les infos de mise à jour de la relation entre l'utilisateur et le projet avec l'id et le nouveau rôle de l'utilisateur
     * @param userIdStr Un faux token d'authorization qui est l'id de l'utilisateur
     * @return Une version simplifiée de la relation entre l'utilisateur et le projet
     */
    @PutMapping("/{id}/change-user-role")
    public ResponseEntity<ProjectUserDto> ChangeUserRole(@PathVariable Long id, @RequestBody ProjectUserIdRequest projectUserIdRequest, @RequestHeader("Authorization") String userIdStr) {
        User user = userService.getUserById(Long.valueOf(userIdStr));
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectService.getProjectById(id);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserService.getByProjectAndUser(project, user);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        if(projectUser.getRole() == UserRole.OBSERVER || projectUser.getRole() == UserRole.MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer or member cannot change the role of other users on a project");
        }

        User updateUser = userService.getUserById(projectUserIdRequest.getUserId());
        if(updateUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : This is not the user you are looking for");
        }

        ProjectUser updateProjectUser = projectUserService.getByProjectAndUser(project, updateUser);
        projectUserService.updateUserRole(updateProjectUser, projectUserIdRequest.getUserRole());
        return ResponseEntity.ok(projectUserService.getProjectUserDto(updateProjectUser));
    }
}
