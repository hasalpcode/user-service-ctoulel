package com.hasalp.ctoulel_user_service.controller;

import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetRequestDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetResponseDTO;
import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;
import com.hasalp.ctoulel_user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserRequestDTO request) {
//        AuthResponseDTO
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponseDTO> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO request) {
        return ResponseEntity.ok(service.requestPasswordReset(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponseDTO> resetPassword(
            @Valid @RequestBody PasswordResetDTO request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }
}

