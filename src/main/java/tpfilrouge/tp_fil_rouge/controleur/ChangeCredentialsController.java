package tpfilrouge.tp_fil_rouge.controleur;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.modele.repository.TuteurEnseignantRepository;
import tpfilrouge.tp_fil_rouge.security.CustomUserDetails;

/**
 * Contrôleur pour la gestion du changement obligatoire d'identifiants
 */
@Controller
public class ChangeCredentialsController {

    private final TuteurEnseignantRepository tuteurEnseignantRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ChangeCredentialsController(TuteurEnseignantRepository tuteurEnseignantRepository, 
                                     PasswordEncoder passwordEncoder) {
        this.tuteurEnseignantRepository = tuteurEnseignantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Affiche la page de changement d'identifiants
     */
    @GetMapping("/change-credentials")
    public String showChangeCredentialsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            model.addAttribute("currentUsername", userDetails.getUsername());
            model.addAttribute("prenom", userDetails.getPrenom());
            
            if (!userDetails.mustChangeCredentials()) {
                // L'utilisateur n'a pas besoin de changer ses identifiants
                return "redirect:/";
            }
        } else {
            return "redirect:/login";
        }
        
        return "change-credentials";
    }

    /**
     * Traite le changement d'identifiants
     */
    @PostMapping("/change-credentials")
    public String processChangeCredentials(@RequestParam("newLogin") String newLogin,
                                         @RequestParam("newPassword") String newPassword,
                                         @RequestParam("confirmPassword") String confirmPassword,
                                         @RequestParam("nom") String nom,
                                         @RequestParam("prenom") String prenom,
                                         RedirectAttributes redirectAttributes,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            redirectAttributes.addFlashAttribute("error", "Session invalide");
            return "redirect:/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        
        // Validations
        if (newLogin == null || newLogin.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le nouveau login est obligatoire");
            return "redirect:/change-credentials";
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Le mot de passe doit contenir au moins 6 caractères");
            return "redirect:/change-credentials";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les mots de passe ne correspondent pas");
            return "redirect:/change-credentials";
        }
        
        if (nom == null || nom.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le nom est obligatoire");
            return "redirect:/change-credentials";
        }
        
        if (prenom == null || prenom.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le prénom est obligatoire");
            return "redirect:/change-credentials";
        }

        try {
            // Vérifier si le nouveau login est déjà utilisé (sauf si c'est le même utilisateur)
            TuteurEnseignant existingTuteur = tuteurEnseignantRepository.findByLogin(newLogin.trim()).orElse(null);
            if (existingTuteur != null && !existingTuteur.getId().equals(userDetails.getTuteurId())) {
                redirectAttributes.addFlashAttribute("error", "Ce login est déjà utilisé");
                return "redirect:/change-credentials";
            }

            // Récupérer et mettre à jour le tuteur
            TuteurEnseignant tuteur = tuteurEnseignantRepository.findById(userDetails.getTuteurId()).orElse(null);
            if (tuteur == null) {
                redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
                return "redirect:/change-credentials";
            }

            // Mettre à jour les informations
            tuteur.setLogin(newLogin.trim());
            tuteur.setMotDePasse(passwordEncoder.encode(newPassword));
            tuteur.setNom(nom.trim());
            tuteur.setPrenom(prenom.trim());
            tuteur.setDoitChangerIdentifiants(false); // Plus besoin de changer
            
            tuteurEnseignantRepository.save(tuteur);

            // Déconnecter l'utilisateur pour qu'il se reconnecte avec ses nouveaux identifiants
            new SecurityContextLogoutHandler().logout(request, response, auth);
            
            redirectAttributes.addFlashAttribute("success", "Vos identifiants ont été mis à jour. Veuillez vous reconnecter.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour : " + e.getMessage());
            return "redirect:/change-credentials";
        }
    }
}