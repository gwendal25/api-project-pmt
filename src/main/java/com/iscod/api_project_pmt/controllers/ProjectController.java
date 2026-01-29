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
     * Cette méthode renvoie la liste de tous les projets dont l'utilisateur fait partie
     * @param userIdStr Un faux token d'accès (id de l'utilisateur)
     * @return une liste de projets simplifiés avec id, nom, description, date de début et rôle de l'utilisateur de chaque projet
     * <strong>Exemple de réponse :</strong>
     * <pre><code>
     *     [
     *     {
     *         "id": 1,
     *         "name": "Project de parc d'attraction en VR",
     *         "description": "Projet de parc d'attractions en Réalité Virtuelle ou l'utilisateur prend le controle d'un T-76 pour tirer sur diverses créatures",
     *         "startDate": "2025-03-01",
     *         "userRole": "ADMIN"
     *     }
     * </code></pre>
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
     * @param id Id du projet
     * @param userIdStr Un faux token d'accès (id de l'utilisateur)
     * @return Les informations du projet avec id, nom, description, date de début, rôle de l'utilisateur, liste des tâches et liste des utilisateurs.
     * <strong>Exemple de réponse :</strong>
     * <pre><code>
     *     {
     *         "id": 1,
     *         "name": "Project de parc d'attraction en VR",
     *         "description": "Projet de parc d'attractions en Réalité Virtuelle ou l'utilisateur prend le controle d'un T-76 pour tirer sur diverses créatures",
     *         "startDate": "2025-03-01",
     *         "userRole": "ADMIN",
     *         "tasks": [
     *              {
     *                  "id": 1,
     *                  "name": "Délimitation de la zone de construction du parc",
     *                  "description": "Effectuer des tours de reconnaissance des différentes zones pour trouver les plus adaptés à la construction du parc.",
     *                  "taskPriority": "HIGH",
     *                  "taskStatus": "IN_PROGRESS",
     *                  "endDate": "2025-07-01",
     *                  "user": {
     *                      "id": 7,
     *                      "name": "Gépard",
     *                  }
     *                  "isNotified": true
     *              }
     *         ],
     *         "users": [
     *              "id": 1,
     *              "name": "Endministrator",
     *              "email": "endmin@factorymail.com",
     *         ]
     *     }
     * </code></pre>
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
     * @param id Id du projet
     * @param userIdStr Un faux token d'accès (id de l'utilisateur)
     * @return les infos du projet avec id, nom, description, date de début et liste des utilisateurs avec leur rôle sur le projet.
     * <strong>Exemple de réponse :</strong>
     * <pre><code>
     *     {
     *         "id": 1,
     *         "name": "Unwelcome Planet",
     *         "description": "Project de jeu d'arcade en vue de dessus ou le joueur contrôle un manequin pour tirer sur diverses robotos",
     *         "startDate": "2025-03-01",
     *         "users": [
     *              {
     *                  "id": 1,
     *                  "name": "Marcus",
     *                  "email": "marcuspolus@factorymail.com",
     *                  "role": "ADMIN"
     *              }
     *         ]
     *     }
     * </pre></code>
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
     * Créer un nouveau projet et l'associe avec l'utilisateur qui le crée en tant qu'administrateur.
     * @param projectRequest Les détails du projet à créer
     * @param userIdStr Un faux token d'accès (l'id de l'utilisateur)
     * @param uriBuilder Le uri Builder qui permet de construire l'url du projet nouvellement crée
     * @return une ResponseEntity qui contient les informations simplifiées du projet (id, nom, description, et date de début).
     * <strong>Exemple de requête :</strong>
     * <pre><code>
     *     {
     *         "name": "Projet MagicBlade",
     *         "description": "un jeu d'action-RPG en VR ou le joueur contrôle un T-76 pour combattre diverses créatures",
     *         "startDate": "2021-03-01"
     *     }
     * </code></pre>
     * <strong>Exemple de réponse</strong>
     * <pre><code>
     *     {
     *         "id": 1,
     *         "name": "Projet MagicBlade",
     *         "description": "un jeu d'action-RPG en VR ou le joueur contrôle un T-76 pour combattre diverses créatures",
     *         "startDate": "2021-03-01"
     *     }
     * </code></pre>
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
     * @param id Id du projet auquel ajouter une nouvelle tâche
     * @param taskRequest Les informations de la tâche à créer
     * @param userIdStr Un faux token d'accès (l'id de l'utilisateur)
     * @param uriBuilder Le uri Builder qui permet de construire l'url de la tâche nouvellement crée
     * @return Une ResponseEntity qui contient les infos de la tâche (id, nom, description, priorité, status, date de fin).
     * <strong>Exemple de requête :</strong>
     * <pre><code>
     *     {
     *         "name": "Ajout du controller de base",
     *         "description": "Ajouter un character controller basique pour marcher, courir et sauter en 3D",
     *         "taskPriority": "HIGH",
     *         "taskStatus": "IN_PROGRESS",
     *         "endDate": "2021-03-09"
     *     }
     * </code></pre>
     * <strong>Exemple de réponse :</strong>
     * <pre><code>
     *     {
     *         "id": 1,
     *         "name": "Ajout du controller de base",
     *         "description": "Ajouter un character controller basique pour marcher, courir et sauter en 3D",
     *         "taskPriority": "HIGH",
     *         "taskStatus": "IN_PROGRESS",
     *         "endDate": "2021-03-09"
     *     }
     * </code></pre>
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
