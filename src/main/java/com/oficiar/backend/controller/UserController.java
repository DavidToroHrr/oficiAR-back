package com.oficiar.backend.controller;

import com.oficiar.backend.dto.UserProfileDTO;
import com.oficiar.backend.entity.User;
import com.oficiar.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.oficiar.backend.dto.UpdateProfileRequest;
import com.oficiar.backend.dto.ChangePasswordRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> profile() {
        // Obtenemos del contexto el correo gracias al JwtFilter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOp = userRepository.findByEmail(email);
        if (userOp.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOp.get();
        UserProfileDTO profile = new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail());

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOp = userRepository.findByEmail(email);
        if (userOp.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOp.get();
        user.setName(request.getName());
        userRepository.save(user);

        return ResponseEntity.ok(new UserProfileDTO(user.getId(), user.getName(), user.getEmail()));
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOp = userRepository.findByEmail(email);
        if (userOp.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOp.get();

        if (!passwordEncoder.matches(request.getPasswordActual(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}
