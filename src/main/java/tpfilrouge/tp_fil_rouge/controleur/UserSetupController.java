package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tpfilrouge.tp_fil_rouge.modele.entite.Utilisateur;
import tpfilrouge.tp_fil_rouge.modele.repository.UtilisateurRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserSetupController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSetupController(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/setup")
    public String setupForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        Utilisateur u = utilisateurRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        u.setPassword("");
        model.addAttribute("utilisateur", u);
        return "user/setup";
    }

    @PostMapping("/setup")
    public String setupSubmit(@AuthenticationPrincipal UserDetails principal, @Valid Utilisateur utilisateur, BindingResult br) {
        if (br.hasErrors()) return "user/setup";
        Utilisateur existing = utilisateurRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        // vérifier unicité si username changé
        if (!existing.getUsername().equals(utilisateur.getUsername())) {
            if (utilisateurRepository.findByUsername(utilisateur.getUsername()).isPresent()) {
                br.rejectValue("username", "duplicate", "Nom d'utilisateur déjà pris");
                return "user/setup";
            }
            existing.setUsername(utilisateur.getUsername());
        }
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }
        existing.setMustChangePassword(false);
        utilisateurRepository.save(existing);
        return "redirect:/";
    }
}
