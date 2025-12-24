package com.hasalp.ctoulel_user_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class  AuthResponseDTO{
    private String token;
//    private Set<RoleResponseDTO> roles;
    private UserResponseDTO user;
}

