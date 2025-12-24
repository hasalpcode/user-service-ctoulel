package com.hasalp.ctoulel_user_service.controller;

import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;
import com.hasalp.ctoulel_user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    @GetMapping("/hello")
    public String hello() {
        return "Bonjour utilisateur auuthentifié !";
    }
}
