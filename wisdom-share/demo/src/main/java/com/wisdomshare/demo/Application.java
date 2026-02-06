package com.wisdomshare.demo;

import com.wisdomshare.demo.role.Role;
import com.wisdomshare.demo.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")   // ← important: links to your AuditorAware bean
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Initializes default roles if they don't exist.
     * This runs once on startup.
     */
    @Bean
    public CommandLineRunner roleInitializer(RoleRepository roleRepository) {
        return args -> {
            // Create USER role if missing
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(
                    Role.builder()
                        .name("USER")
                        .build()
                );
                System.out.println("Created default role: USER");
            }

            // Create ADMIN role if missing (very common in book/social apps)
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(
                    Role.builder()
                        .name("ADMIN")
                        .build()
                );
                System.out.println("Created default role: ADMIN");
            }
        };
    }
}