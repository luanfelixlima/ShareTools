package com.fesa.sharetools.Config;

import com.fesa.sharetools.Model.Role;
import com.fesa.sharetools.Model.User;
import com.fesa.sharetools.Repository.RoleRepository;
import com.fesa.sharetools.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner seedData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // Verifica se ROLE ADMIN já existe
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("ADMIN");
                        return roleRepository.save(role);
                    });

            // Se não houver nenhum ADMIN no sistema, cria um
            if (!userRepository.existsByRole_Name("ADMIN")) {
                User admin = new User();
                admin.setEmail("admin@sharetools.com");
                admin.setPassword(passwordEncoder.encode("adminadmin"));
                admin.setRole(adminRole);
                admin.setName("admin");
                admin.setPhone("(00) 00000-0000");
                userRepository.save(admin);
                System.out.println("✅ Usuário ADMIN criado automaticamente.");
            } else {
                System.out.println("ℹ️ Já existe pelo menos um ADMIN cadastrado.");
            }

        };
    }
}
