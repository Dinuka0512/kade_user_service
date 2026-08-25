package com.dinuka.dev.user_service.service;

import com.dinuka.dev.user_service.dto.AuthResponse;
import com.dinuka.dev.user_service.dto.LoginRequest;
import com.dinuka.dev.user_service.dto.RegisterRequest;
import com.dinuka.dev.user_service.model.User;
import com.dinuka.dev.user_service.repository.UserRepository;
import com.dinuka.dev.user_service.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getName());
        return new AuthResponse(token, new AuthResponse.UserDto(
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRole()
        ));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getName());
        return new AuthResponse(token, new AuthResponse.UserDto(
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRole()
        ));
    }
}
