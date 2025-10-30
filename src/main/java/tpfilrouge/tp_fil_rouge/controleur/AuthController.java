package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tpfilrouge.tp_fil_rouge.security.CustomUserDetails;

/**
 * Contrôleur d'authentification simplifié
 * Conforme aux exigences du professeur : login + accueil + déconnexion
 */
@Controller
public class AuthController {

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
            // Vérifier si l'utilisateur doit changer ses identifiants
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                
                if (userDetails.mustChangeCredentials()) {
                    // Rediriger vers la page de changement d'identifiants
                    return "redirect:/change-credentials";
                }
                
                model.addAttribute("prenom", userDetails.getPrenom());
                model.addAttribute("username", userDetails.getUsername());
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
