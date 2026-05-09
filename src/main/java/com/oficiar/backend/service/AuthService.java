package com.oficiar.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.oficiar.backend.dto.LoginOutDTO;
import com.oficiar.backend.dto.LoginRequest;
import com.oficiar.backend.dto.RegisterRequest;
import com.oficiar.backend.entity.User;
import com.oficiar.backend.repository.UserRepository;
import com.oficiar.backend.security.JwtUtil;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<java.util.Map<String, String>> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Error, el usuario ya se encuentra registrado en el sistema...");
    }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Usuario registrado correctamente");
        return ResponseEntity.ok(response);
   
    }
    

        // 1. Cambiamos "String" por "ResponseEntity<LoginOutDTO>"
    public ResponseEntity<LoginOutDTO> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 2. Guardamos el token en una variable temporal
        String tokenGenerado = jwtUtil.generateToken(user.getEmail());

        // 3. Empaquetamos el token y el nombre en el DTO y lo enviamos con un status 200 (OK)
        return ResponseEntity.ok(new LoginOutDTO(tokenGenerado, user.getName(), user.getEmail()));
    }
}
