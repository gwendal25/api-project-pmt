package com.iscod.api_project_pmt.controllers;

import com.iscod.api_project_pmt.dtos.UserDto;
import com.iscod.api_project_pmt.dtos.UserLoginDto;
import com.iscod.api_project_pmt.dtos.UserRequest;
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

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

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

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.getEmail()).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        if(!userLoginDto.getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
