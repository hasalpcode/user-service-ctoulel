package com.hasalp.ctoulel_user_service.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotBlank
    private String username;



//    @Email
    @Nullable
    private String email;

}

