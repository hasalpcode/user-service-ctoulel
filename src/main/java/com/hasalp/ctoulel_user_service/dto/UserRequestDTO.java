package com.hasalp.ctoulel_user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String role;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}

