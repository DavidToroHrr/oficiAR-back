package com.oficiar.backend.config;

import com.oficiar.backend.entity.*;

import com.oficiar.backend.entity.User;
import com.oficiar.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Configuration
public class DataLoader {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Le decimos que use el estándar de BCrypt
    }

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            RestTemplate restTemplate,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // 1. Insertar admin default solo si no existe
            if (!userRepository.existsByEmail("admin@exodus.com")) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@exodus.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("CLIENT");
                userRepository.save(admin);
                System.out.println("Usuario administrador por defecto creado.");
            }

            // 2. Consumir API externa y quemar datos en la DB local
            String url = "https://jsonplaceholder.typicode.com/users";
            // Obtenemos una lista de mapas (objetos JSON)
            List<Map<String, Object>> externalUsers = restTemplate.getForObject(url, List.class);

            if (externalUsers != null) {
                for (Map<String, Object> userData : externalUsers) {
                    String email = (String) userData.get("email");

                    if (!userRepository.existsByEmail(email)) {
                        User user = new User();
                        user.setName((String) userData.get("name"));
                        user.setEmail(email);
                        user.setRole("CLIENT");
                        // Como la API no trae passwords, les asignamos una genérica
                        // El nombre de usuario en minúsculas + "123"
                        String username = (String) userData.get("username");
                        user.setPassword(passwordEncoder.encode(username + "123"));

                        userRepository.save(user);
                    }
                }
                System.out.println(externalUsers.size() + " usuarios integrados desde JSONPlaceholder");
            }
        };
    }
}