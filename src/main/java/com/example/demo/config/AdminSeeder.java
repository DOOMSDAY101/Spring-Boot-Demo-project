package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.Users;
import com.example.demo.repository.UserRepo;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(
            UserRepo repository,
            BCryptPasswordEncoder encoder) {

        return args -> {

            if (repository.existsByUsername("admin")) {
                return;
            }

            Users admin = new Users();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));

            repository.save(admin);
        };
    }
}