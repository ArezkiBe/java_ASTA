package tpfilrouge.tp_fil_rouge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tpfilrouge.tp_fil_rouge.modele.entite.Utilisateur;
import tpfilrouge.tp_fil_rouge.modele.repository.UtilisateurRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";
            if (utilisateurRepository.findByUsername(adminUsername).isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRoles("ROLE_ADMIN,ROLE_USER");
                admin.setPrenom("SuperAdmin");
                admin.setActif(true);
                utilisateurRepository.save(admin);
                System.out.println("Utilisateur admin créé en base (admin/admin)");
            }
        };
    }
}

