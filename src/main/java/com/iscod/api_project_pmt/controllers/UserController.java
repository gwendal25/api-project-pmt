package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.user.UserDto;
import com.iscod.api_project_pmt.dtos.user.UserLoginRequest;
import com.iscod.api_project_pmt.dtos.user.UserRequest;
import com.iscod.api_project_pmt.entities.User;
import com.iscod.api_project_pmt.mappers.UserMapper;
import com.iscod.api_project_pmt.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Cette méthode retourne la liste de tous les utilisateurs.
     * Utile pour le debug de l'application
     * @return La liste des utilisateurs avec id, nom et email
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retourne les informations d'un utilisateur grâce à son id
     * @param id l'id de l'utilisateur dont on veut récupérer les informations
     * @return les informations de l'utilisateur avec id, nom et email
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Créer un utilisateur avec les informations envoyées dans userRequest
     * @param userRequest les informations de l'utilisateur avec nom, email, et mot de passe
     * @param uriBuilder Le builder pour renvoyer l'url de l'utilisateur
     * @return Les informations de l'utilisateur crée avec l'url pour accéder aux informations cet utilisateur
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRequest userRequest, UriComponentsBuilder uriBuilder) {
        if(!userRequest.getPassword().equals(userRequest.getRepeatPassword())) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findByEmail(userRequest.getEmail()).orElse(null);
        if(user != null) {
            return ResponseEntity.badRequest().build();
        }

        User newUser = userMapper.toUser(userRequest);
        userRepository.save(newUser);
        UserDto userDto = userMapper.toDto(newUser);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    /**
     * Permet à un utilisateur de faire une fausse connexion en renvoyant les infos de l'utilisateur si il a envoyé la bonne combinaison de mot de passe/email
     * @param userLoginRequest La requête de connexion avec email et mot de passe
     * @return Les informations de l'utilisateur avec id, nom et email
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.getEmail()).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        if(!userLoginRequest.getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
