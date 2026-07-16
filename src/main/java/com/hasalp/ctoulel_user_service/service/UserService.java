package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetRequestDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetResponseDTO;
import com.hasalp.ctoulel_user_service.dto.UserRequest;
import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO register(UserRequestDTO dto);
    UserResponseDTO getById(Long id);
    List<UserResponseDTO> getUsers();
    UserResponseDTO update(Long userId, UserRequest dto);
    UserResponseDTO updateUserRole(Long userId, Long roleId);

    List<UserResponseDTO> findByIds(List<Long> id);
    AuthResponseDTO login(UserRequestDTO dto);
    void deleteUser(Long id);
    
    // Méthodes pour la réinitialisation de mot de passe
    PasswordResetResponseDTO requestPasswordReset(PasswordResetRequestDTO request);
    PasswordResetResponseDTO resetPassword(PasswordResetDTO request);
}
