package com.example.lets_play.config;

import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader to create default admin user on application startup
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin if it doesn't exist
        if (userRepository.findByEmail("admin@letsplay.com").isEmpty()) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@letsplay.com");
            admin.setPassword(passwordEncoder.encode("Admin123*"));
            admin.setRole("ADMIN");
            
            userRepository.save(admin);
            System.out.println("Default admin created: admin@letsplay.com / Admin123*");
        }
    }
}