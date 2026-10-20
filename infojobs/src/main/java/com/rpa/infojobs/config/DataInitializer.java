package com.rpa.infojobs.config;


import com.rpa.infojobs.model.User;
import com.rpa.infojobs.model.UserRole;
import com.rpa.infojobs.repository.UserRepository;
import com.rpa.infojobs.security.AesEncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesEncryptionService aesEncryptionService;

    //  Ajuste aqui os dados do admin inicial
    private static final String DEFAULT_ADMIN_EMAIL = "admin@sistema.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_INFOJOBS_EMAIL = "junior.jblins@gmail.com";
    private static final String DEFAULT_INFOJOBS_PASSWORD = "juninho";

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByEmail(DEFAULT_ADMIN_EMAIL)) {
            log.info("Usuário ADMIN já existe. Pulando seed.");
            return;
        }

        log.info("Criando usuário ADMIN inicial: {}", DEFAULT_ADMIN_EMAIL);

        User admin = User.builder()
                .email(DEFAULT_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .name("Admin")
                .infojobsEmail(DEFAULT_INFOJOBS_EMAIL)
                .infojobsPassword(aesEncryptionService.encrypt(DEFAULT_INFOJOBS_PASSWORD))
                .role(UserRole.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        log.info(" Usuário ADMIN criado com sucesso!");
        log.info("   Email: {}", DEFAULT_ADMIN_EMAIL);
        log.info("   Senha: {}", DEFAULT_ADMIN_PASSWORD);
    }
}


