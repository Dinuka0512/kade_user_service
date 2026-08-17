package com.dinuka.dev.user_service.config;

import com.dinuka.dev.user_service.model.User;
import com.dinuka.dev.user_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(new User("Admin User", "admin@kade.lk",
                passwordEncoder.encode("admin123"), "admin"));
        userRepository.save(new User("Vendor User", "vendor@kade.lk",
                passwordEncoder.encode("vendor123"), "vendor"));
        userRepository.save(new User("Sanduni Perera", "sanduni@example.com",
                passwordEncoder.encode("password123"), "customer"));
    }
}
