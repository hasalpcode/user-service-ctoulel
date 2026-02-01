package com.hasalp.ctoulel_user_service.mapper;

import com.hasalp.ctoulel_user_service.dto.UserRequest;
import org.mapstruct.*;
import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;
import com.hasalp.ctoulel_user_service.model.Role;
import com.hasalp.ctoulel_user_service.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", source = "role")
    User toEntity(UserRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserRequest dto, @MappingTarget User user);

    @Mapping(target = "role", source = "role")
    UserResponseDTO toDTO(User user);

    // MapStruct will automatically use this method to map String -> Role
    default Role map(String roleName) {
        if (roleName == null) return null;
        Role role = new Role();
        role.setName(roleName);
        return role;
    }

    // MapStruct will automatically use this method to map Role -> String
    default String map(Role role) {
        if (role == null) return null;
        return role.getName();
    }
}
