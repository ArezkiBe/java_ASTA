package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tpfilrouge.tp_fil_rouge.service.PasswordResetService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String forgotForm() {
        return "auth/forgot";
    }

    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam String username, Model model) {
        var tokenOpt = passwordResetService.createPasswordResetTokenForUser(username);
        if (tokenOpt.isEmpty()) {
            model.addAttribute("message", "Aucun utilisateur trouvé pour ce nom d'utilisateur.");
            return "auth/forgot";
        }
        String token = tokenOpt.get();
        // Dans une vraie app, on enverrait le lien par email. Ici on affiche le lien pour tester.
        String resetLink = "/reset-password?token=" + token;
        model.addAttribute("message", "Lien de réinitialisation (en dev) : " + resetLink);
        return "auth/forgot";
    }

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        boolean valid = passwordResetService.validatePasswordResetToken(token);
        model.addAttribute("token", token);
        if (!valid) {
            model.addAttribute("error", "Token invalide ou expiré.");
            return "auth/reset";
        }
        return "auth/reset";
    }

    @PostMapping("/reset-password")
    public String processReset(@RequestParam String token, @RequestParam String password, Model model) {
        boolean ok = passwordResetService.resetPassword(token, password);
        if (!ok) {
            model.addAttribute("error", "Impossible de réinitialiser le mot de passe (token invalide ou expiré).");
            return "auth/reset";
        }
        model.addAttribute("message", "Mot de passe réinitialisé avec succès. Vous pouvez vous connecter.");
        return "auth/reset";
    }
}

