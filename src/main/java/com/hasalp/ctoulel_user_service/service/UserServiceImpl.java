package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.dao.UserDao;
import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.UserRequestDTO;
import com.hasalp.ctoulel_user_service.dto.UserResponseDTO;
import com.hasalp.ctoulel_user_service.exception.ResourceExistsException;
import com.hasalp.ctoulel_user_service.exception.ResourceNotFoundException;
import com.hasalp.ctoulel_user_service.mapper.UserMapper;
import com.hasalp.ctoulel_user_service.model.Role;
import com.hasalp.ctoulel_user_service.model.User;
import com.hasalp.ctoulel_user_service.repository.RoleRepository;
import com.hasalp.ctoulel_user_service.repository.UserRepository;
import com.hasalp.ctoulel_user_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserDao repository;
    private final UserRepository repos;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public UserResponseDTO register(UserRequestDTO userDto) {
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new ResourceExistsException("Email déjà utilisé");
        }

        Role role = roleRepository.findByName(userDto.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        User user = mapper.toEntity(userDto);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return mapper.toDTO(repository.save(user));
    }

    @Override
    public UserResponseDTO getById(Long id) {
        User u = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return mapper.toDTO(u);
    }

    @Override
    public List<UserResponseDTO> getUsers() {
        List<User> users = repository.getUsers();

        if (users.isEmpty()) {
            // Lancer l'exception si aucun utilisateur n'est trouvé
            throw new ResourceNotFoundException("Aucun utilisateur trouvé");
        }

        return users.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!repos.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur avec id " + id + " non trouvé");
        }
        repository.delete(id);
    }





    @Override
    public AuthResponseDTO login(UserRequestDTO userDto) {
        System.out.println("xxxx==>"+userDto.getUsername()+"==>"+userDto.getPassword()+"==>"+userDto.getEmail());
        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );
        System.out.println("xxxx==>"+userDto);
        User user = repository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

//        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

//        System.out.println("nnnn==>"+userDetails);
//        String jwtToken = jwtService.generateToken(userDetails);

        String jwtToken = jwtService.generateToken(user);


        return AuthResponseDTO.builder()
                .token(jwtToken)
                .user(mapper.toDTO(user))
                .build();
    }

}
