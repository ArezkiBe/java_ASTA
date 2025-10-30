package tpfilrouge.tp_fil_rouge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.modele.repository.TuteurEnseignantRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(TuteurEnseignantRepository tuteurRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Créer uniquement l'utilisateur admin générique à la première initialisation
            String adminLogin = "admin";
            if (!tuteurRepository.existsByLogin(adminLogin)) {
                TuteurEnseignant admin = new TuteurEnseignant();
                admin.setLogin(adminLogin);
                admin.setMotDePasse(passwordEncoder.encode("admin"));
                admin.setNom("Administrateur");
                admin.setPrenom("Système");
                admin.setDoitChangerIdentifiants(true); // Forcer le changement à la première connexion
                tuteurRepository.save(admin);
                System.out.println("Utilisateur admin générique créé (admin/admin)");
                System.out.println("ATTENTION : Vous devez changer vos identifiants à la première connexion !");
            }
        };
    }
}

