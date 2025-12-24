package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO register(UserRequestDTO dto);
    UserResponseDTO getById(Long id);
    List<UserResponseDTO> getUsers();
    AuthResponseDTO login(UserRequestDTO dto);
    void deleteUser(Long id);
}
