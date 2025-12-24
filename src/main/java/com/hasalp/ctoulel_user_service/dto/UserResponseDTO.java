package com.hasalp.ctoulel_user_service.dto;

import com.hasalp.ctoulel_user_service.model.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class UserResponseDTO {
    private Long userId;
    private String username;
    private Role role;
    private String email;
    private LocalDateTime dateInscription;
}