package com.example.projekt;

import com.example.projekt.model.Role;
import com.example.projekt.model.User;
import com.example.projekt.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createAdminIfNotExists() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setAge(19);
            admin.setRole(Role.ROLE_ADMIN);
            admin.setName("Admin");
            admin.setSurname("Admin");
            userRepository.save(admin);
            System.out.println(">>> Admin created");
        } else {
            System.out.println(">>> Admin already exists");
        }
    }
}
