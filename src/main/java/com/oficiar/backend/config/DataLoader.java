package com.oficiar.backend.config;

import com.oficiar.backend.entity.*;

import com.oficiar.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {   

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository
            
    ) {
        return args -> {
            // 1. Insertar admin default solo si no existe
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!userRepository.existsByEmail("admin@exodus.com")) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@exodus.com");
                admin.setPassword(encoder.encode("admin123"));
                userRepository.save(admin);
                System.out.println("Usuario administrador por defecto creado.");
            }

            

        };
    }
}