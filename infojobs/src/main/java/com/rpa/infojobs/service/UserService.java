package com.rpa.infojobs.service;

import com.rpa.infojobs.dto.request.UserRequest;
import com.rpa.infojobs.dto.response.UserResponse;
import com.rpa.infojobs.model.User;
import com.rpa.infojobs.model.UserRole;
import com.rpa.infojobs.repository.UserRepository;
import com.rpa.infojobs.security.AesEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesEncryptionService aesEncryptionService;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }

        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .infojobsEmail(request.getInfojobsEmail())
                    .infojobsPassword(aesEncryptionService.encrypt(request.getInfojobsPassword()))
                    .role(UserRole.valueOf(request.getRole().toUpperCase()))
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            User saved = userRepository.save(user);
            return toResponse(saved);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar senha do Infojobs", e);
        }
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .infojobsEmail(user.getInfojobsEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
