package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tpfilrouge.tp_fil_rouge.modele.entite.Utilisateur;
import tpfilrouge.tp_fil_rouge.modele.repository.UtilisateurRepository;

/**
 * Contrôleur d'authentification simplifié
 * Conforme aux exigences du professeur : login + accueil + déconnexion
 */
@Controller
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public AuthController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * 1.1 & 1.2 - Page de connexion avec réaffichage en cas d'erreur
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 1.3 - Page d'accueil avec "Bonjour [Prénom] | Déconnectez-vous"
     */
    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            // Récupérer les informations de l'utilisateur connecté
            Utilisateur utilisateur = utilisateurRepository.findByUsername(auth.getName()).orElse(null);
            if (utilisateur != null) {
                model.addAttribute("prenom", utilisateur.getPrenom());
                model.addAttribute("username", utilisateur.getUsername());
            }
        }

        return "home";
    }

    /**
     * Redirection après connexion réussie (si nécessaire)
     */
    @GetMapping("/accueil")
    public String accueil() {
        return "redirect:/";
    }
}
