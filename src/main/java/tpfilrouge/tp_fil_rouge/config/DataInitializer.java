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
            // Créer l'utilisateur admin
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
            
            // Créer les comptes utilisateurs pour les tuteurs enseignants
            String[][] tuteurs = {
                {"prof.martin", "Jean-Claude"},
                {"prof.bernard", "Sylvie"},
                {"prof.dubois", "Philippe"}, 
                {"prof.moreau", "Catherine"},
                {"prof.simon", "François"}
            };
            
            for (String[] tuteur : tuteurs) {
                String login = tuteur[0];
                String prenom = tuteur[1];
                
                if (utilisateurRepository.findByUsername(login).isEmpty()) {
                    Utilisateur utilisateurTuteur = new Utilisateur();
                    utilisateurTuteur.setUsername(login);
                    utilisateurTuteur.setPassword(passwordEncoder.encode("password123")); // Même mot de passe que dans la base tuteur
                    utilisateurTuteur.setRoles("ROLE_TUTEUR,ROLE_USER");
                    utilisateurTuteur.setPrenom(prenom);
                    utilisateurTuteur.setActif(true);
                    utilisateurRepository.save(utilisateurTuteur);
                    System.out.println("Utilisateur tuteur créé : " + login + " / password123");
                }
            }
        };
    }
}

