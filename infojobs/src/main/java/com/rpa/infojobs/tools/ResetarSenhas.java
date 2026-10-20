package com.rpa.infojobs.tools;

import com.rpa.infojobs.model.User;
import com.rpa.infojobs.repository.UserRepository;
import com.rpa.infojobs.security.AesEncryptionService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})

@ComponentScan(
        basePackages = "com.rpa.infojobs.security",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AesEncryptionService.class
        ),
        useDefaultFilters = false
)
@EnableJpaRepositories(basePackages = "com.rpa.infojobs.repository")
@EntityScan(basePackages = "com.rpa.infojobs.model")
public class ResetarSenhas {

    //  AJUSTE AQUI:
    private static final String EMAIL_ALVO          = "admin@sistema.com"; // login do sistema
    private static final String NOVA_SENHA_SISTEMA  = "admin123";          // nova senha de login
    private static final String NOVA_SENHA_INFOJOBS = "juninho";           // nova senha do InfoJobs

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ResetarSenhas.class)
                .web(WebApplicationType.NONE)
                .run(args);

        UserRepository userRepository = context.getBean(UserRepository.class);
        AesEncryptionService aesEncryptionService = context.getBean(AesEncryptionService.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        try {
            User user = userRepository.findByEmail(EMAIL_ALVO)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + EMAIL_ALVO));

            System.out.println("============================================================");
            System.out.println("Usuário encontrado: " + user.getEmail());
            System.out.println("ID: " + user.getId());
            System.out.println("Active: " + user.isActive());
            System.out.println("============================================================");

            // 1) Senha do sistema (BCrypt)
            String novoHashSistema = passwordEncoder.encode(NOVA_SENHA_SISTEMA);
            user.setPassword(novoHashSistema);
            System.out.println("Hash BCrypt (sistema) gerado:");
            System.out.println("   " + novoHashSistema);

            // 2) Senha do InfoJobs (AES)
            String novoHashInfojobs = aesEncryptionService.encrypt(NOVA_SENHA_INFOJOBS);
            user.setInfojobsPassword(novoHashInfojobs);
            System.out.println("Hash AES (InfoJobs) gerado:");
            System.out.println("   " + novoHashInfojobs);

            user.setActive(true);
            userRepository.save(user);

            System.out.println("============================================================");
            System.out.println(" Senhas atualizadas com sucesso!");
            System.out.println("   Login do sistema: " + EMAIL_ALVO + " / " + NOVA_SENHA_SISTEMA);
            System.out.println("   InfoJobs:         " + user.getInfojobsEmail() + " / " + NOVA_SENHA_INFOJOBS);
            System.out.println("============================================================");

        } catch (Exception e) {
            System.err.println(" Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
        }

        System.exit(0);
    }
}
