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
import com.iscod.api_project_pmt.mappers.*;
import com.iscod.api_project_pmt.repositories.ProjectRepository;
import com.iscod.api_project_pmt.repositories.ProjectUserRepository;
import com.iscod.api_project_pmt.repositories.TaskRepository;
import com.iscod.api_project_pmt.repositories.UserRepository;
import com.iscod.api_project_pmt.services.EmailService;
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
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper projectMapper;
    private final SimpleProjectMapper simpleProjectMapper;
    private final ProjectUserMapper projectUserMapper;
    private final ProjectUserRoleMapper projectUserRoleMapper;
    private final TaskMapper taskMapper;
    private final SimpleTaskMapper simpleTaskMapper;
    private final SimpleProjectWithUserRolesMapper simpleProjectWithUserRolesMapper;
    private final EmailService emailService;

    /**
     * Cette méthode renvoie la liste de tous les projets dans la base de données
     * À utiliser pour debugger l'application
     * @return une liste de projets simplifiés avec id, nom, description et date de début de chaque projet
     */
    @GetMapping
    public List<SimpleProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(simpleProjectMapper::toDto)
                .toList();
    }

    /**
     * Cette méthode renvoie la liste de tous les projets auquel l'utilisateur à accès
     * @param userIdStr Un faux token d'authorisation qui est l'id de l'utilisateur qui crée le projet
     * @return une liste de projets simplifiés avec id, nom, description et date de début de chaque projet
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProjectWithUserRolesDto>> getAllProjectsByUser(@RequestHeader("Authorization") String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to log in in order to access your list of projects");
        }

        return ResponseEntity.ok(simpleProjectWithUserRolesMapper.toDtoList(user));
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
        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(projectMapper.toDto(project, user, projectUser.getRole()));
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
        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        return ResponseEntity.ok(projectUserRoleMapper.toUserRoleDto(project));
    }

    /**
     * Créer un nouveau projet et l'enregistre dans la base de données.
     * L'utilisateur qui crée le projet est associé au projet en tant qu'administrateur du projet
     * Les infos du projet sont nom, description et date de début.
     * @param projectRequest Les informations du projet à créer
     * @param userIdStr Un faux token d'authorisation qui est l'id de l'utilisateur qui crée le projet
     * @param uriBuilder Le builder de l'url du projet
     * @return Les informations simplifiées du projet avec uniquement id, nom, description et date de début
     */
    @PostMapping
    public ResponseEntity<SimpleProjectDto> CreateProject(@RequestBody ProjectRequest projectRequest, @RequestHeader("Authorization") String userIdStr, UriComponentsBuilder uriBuilder) {
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectMapper.toProject(projectRequest);
        project = projectRepository.save(project);

        ProjectUser projectUser = new ProjectUser(project, user, UserRole.ADMIN);
        projectUserRepository.save(projectUser);

        SimpleProjectDto projectDto = simpleProjectMapper.toDto(project);
        var uri = uriBuilder.path("/projects/{id}").buildAndExpand(projectDto.getId()).toUri();
        return ResponseEntity.created(uri).body(projectDto);
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
        Project project = projectRepository.findById(id).orElse(null);

        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        UserRole role = projectUser.getRole();
        if(role == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot create tasks");
        }

        Task task = taskMapper.toTask(taskRequest);
        task.setProject(project);
        project.addTask(task);
        taskRepository.save(task);
        projectRepository.save(project);
        SimpleTaskDto taskDto = simpleTaskMapper.toDto(task);
        var uri = uriBuilder.path("/tasks/{id}").buildAndExpand(taskDto.getId()).toUri();
        return ResponseEntity.created(uri).body(taskDto);
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
        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        UserRole role = projectUser.getRole();
        if(role == UserRole.OBSERVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer cannot create tasks");
        }

        projectMapper.update(projectRequest, project);
        projectRepository.save(project);
        return ResponseEntity.ok(simpleProjectMapper.toDto(project));
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
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        UserRole role = projectUser.getRole();
        if(role == UserRole.OBSERVER || role == UserRole.MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer or member cannot add users to a project");
        }

        User newUser = userRepository.findByEmail(projectUserRequest.getEmail()).orElse(null);
        if(newUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : The user you are trying to invite could not be found");
        }

        ProjectUser newProjectUser = new ProjectUser(project, newUser, projectUserRequest.getUserRole());
        projectUserRepository.save(newProjectUser);

        emailService.SendProjectInvite(user.getName(), newUser.getEmail(), project.getName(), projectUserRequest.getUserRole().toString());

        return ResponseEntity.ok(projectUserMapper.toDto(newProjectUser));
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
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You need to be logged in to access this project");
        }

        Project project = projectRepository.findById(id).orElse(null);
        if(project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found : This is not the project you are looking for");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user).orElse(null);
        if(projectUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : You do not have access to this project");
        }

        UserRole role = projectUser.getRole();
        if(role == UserRole.OBSERVER || role == UserRole.MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied : users of role observer or member cannot change the role of other users on a project");
        }

        User updateUser = userRepository.findById(projectUserIdRequest.getUserId()).orElse(null);
        if(updateUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : This is not the user you are looking for");
        }

        ProjectUser updateProjectUser = projectUserRepository.findByProjectAndUser(project, updateUser).orElse(null);
        updateProjectUser.setRole(projectUserIdRequest.getUserRole());
        projectUserRepository.save(updateProjectUser);

        return ResponseEntity.ok(projectUserMapper.toDto(updateProjectUser));
    }
}
