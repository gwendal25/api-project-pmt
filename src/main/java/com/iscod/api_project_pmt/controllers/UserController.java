package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.dtos.user.UserLoginRequest;
import com.iscod.api_project_pmt.dtos.user.UserRequest;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.services.UserService;
import jakarta.validation.Valid;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * Retourne la liste de tous les utilisateurs (debug only)
     * @return La liste des utilisateurs (id, nom et email)
     * <strong>Exemple de réponse</strong>
     * <pre>{@code
     *  [
     *      {
     *          "id": 1,
     *          "name": "Natasha",
     *          "email": "natasha@honkaimail.com"
     *      },
     *      {
     *          "id": 2,
     *          "name": "Gépard",
     *          "email": "gepardmail@honkaimail.com"
     *      }
     *  ]
     * }</pre>
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retourne les informations d'un utilisateur grâce à son id
     * @param id l'id de l'utilisateur dont on veut récupérer les informations
     * @return les informations de l'utilisateur (id, nom et email)
     * <strong>Exemple de réponse :</strong>
     * <pre>{@code
     *  {
     *         "id": 1,
     *         "name": "Natasha",
     *         "email": "natasha@honkaimail.com"
     *  }
     * }</pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : This user does not exist");
        }

        return ResponseEntity.ok(userService.getDto(user));
    }

    /**
     * Créer un nouvel utilisateur
     * @param userRequest les informations de l'utilisateur à créer (nom, email, mot de passe)
     * @param uriBuilder Le uri Builder qui permet de construire l'url de l'utilisateur nouvellement crée
     * @return Les informations de l'utilisateur nouvellement crée (id, nom, email, mot de passe)
     * <strong>Exemple de requête :</strong>
     * <pre>{@code
     *  {
     *        "name": "Natasha",
     *        "email": "natasha@honkaimail.com"
     *        "password": "undergroundPlayer420**",
     *        "repeatPassword": "undergroundPlayer420**"
     * }
     * }</pre>
     * <strong>Exemple de réponse :</strong>
     * <pre>{@code
     * {
     *        "id": 1,
     *        "name": "Natasha",
     *        "email": "natasha@honkaimail.com"
     *        "password": "undergroundPlayer420**"
     * }
     * }</pre>
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRequest userRequest, UriComponentsBuilder uriBuilder) {
        if(!userRequest.getPassword().equals(userRequest.getRepeatPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request : The password and repeat password do not match");
        }

        User user = userService.getByEmail(userRequest.getEmail());
        if(user != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with email found : This email is already used by another user");
        }

        User newUser = userService.create(userRequest);
        return ResponseEntity.created(uriBuilder
                .path("/users/{id}")
                .buildAndExpand(newUser.getId()).toUri())
                .body(userService.getDto(newUser));
    }

    /**
     * Permet à un utilisateur de faire une fausse connexion en renvoyant les infos de l'utilisateur si il a envoyé la bonne combinaison de mot de passe/email
     * @param userLoginRequest La requête de connexion avec email et mot de passe
     * @return Les informations de l'utilisateur avec id, nom et email
     * <strong>Exemple de requête :</strong>
     * <pre>{@code
     *  {
     *        "email": "natasha@honkaimail.com"
     *        "password": "undergroundPlayer420**"
     * }
     * }</pre>
     * <strong>Exemple de réponse :</strong>
     * <pre>{@code
     *  {
     *        "id": 1,
     *        "name": "Natasha",
     *        "email": "natasha@honkaimail.com"
     * }
     * }</pre>
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        User user = userService.getByEmail(userLoginRequest.getEmail());
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found : This is not the user you are looking for");
        }

        if(!userLoginRequest.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect email/password : The email/password combination is incorrect");
        }

        return ResponseEntity.ok(userService.getDto(user));
    }
}
