package com.hasalp.ctoulel_user_service.service;

import com.hasalp.ctoulel_user_service.dao.UserDao;
import com.hasalp.ctoulel_user_service.dto.AuthResponseDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetRequestDTO;
import com.hasalp.ctoulel_user_service.dto.PasswordResetResponseDTO;
import com.hasalp.ctoulel_user_service.dto.UserRequest;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    @Transactional
    public UserResponseDTO update(Long userId, UserRequest dto) {

        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable"));

        mapper.updateFromDto(dto, user);

//        if (dto.posteId() != null) {
//            Poste poste = posteRepository.findById(dto.posteId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Poste introuvable"));
//            membre.setPoste(poste);
//        }

//        if (dto.username() != null || dto.email() != null) {
//            userClient.updateUser(
//                    membre.getUserId(),
//                    new userRequest(dto.username(), dto.email())
//            );
//        }
        return mapper.toDTO(user);
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
    public List<UserResponseDTO> findByIds(List<Long> ids) {
        return repository.findAllById(ids).stream()
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
        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );
        System.out.println("xxxx==>"+userDto);
        User user = repository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        System.out.println("userrrr==>"+mapper.toDTO(user));
        String jwtToken = jwtService.generateToken(user);



        return AuthResponseDTO.builder()
                .token(jwtToken)
                .user(mapper.toDTO(user))
                .build();
    }

    @Override
    @Transactional
    public PasswordResetResponseDTO requestPasswordReset(PasswordResetRequestDTO request) {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur avec l'email " + request.getEmail() + " non trouvé"));

        // Générer un token unique pour la réinitialisation
        String resetToken = UUID.randomUUID().toString();
        
        // Définir l'expiration du token à 24 heures
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(24);

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(expiryTime);
        repository.save(user);

        log.info("Token de réinitialisation généré pour l'utilisateur: {}", request.getEmail());

        return PasswordResetResponseDTO.builder()
                .success(true)
                .message("Un lien de réinitialisation a été envoyé à votre email")
                .resetToken(resetToken)  // À envoyer par email en production
                .build();
    }

    @Override
    @Transactional
    public PasswordResetResponseDTO resetPassword(PasswordResetDTO request) {
        // Valider que les deux mot de passes correspondent
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        // Valider la longueur minimale du mot de passe
        if (request.getNewPassword().length() < 4) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Trouver l'utilisateur par token
        User user = repository.findByResetToken(request.getToken())
                .orElseThrow(() -> new com.hasalp.ctoulel_user_service.exception.InvalidResetTokenException("Token de réinitialisation invalide"));

        // Vérifier que le token n'a pas expiré
        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new com.hasalp.ctoulel_user_service.exception.InvalidResetTokenException("Le token de réinitialisation a expiré");
        }

        // Encoder et définir le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        // Nettoyer le token après utilisation
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        repository.save(user);

        log.info("Mot de passe réinitialisé avec succès pour l'utilisateur: {}", user.getEmail());

        return PasswordResetResponseDTO.builder()
                .success(true)
                .message("Votre mot de passe a été réinitialisé avec succès")
                .build();
    }

}
