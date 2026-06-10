package com.coursparticuliers.config;

import com.coursparticuliers.entity.Utilisateur;
import com.coursparticuliers.entity.enums.Role;
import com.coursparticuliers.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!utilisateurRepository.existsByEmail("admin@coursparticuliers.fr")) {
            utilisateurRepository.save(Utilisateur.builder()
                    .nom("Admin")
                    .prenom("Système")
                    .email("admin@coursparticuliers.fr")
                    .telephone("0600000000")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .role(Role.ADMINISTRATEUR)
                    .actif(true)
                    .build());
        }
    }
}
