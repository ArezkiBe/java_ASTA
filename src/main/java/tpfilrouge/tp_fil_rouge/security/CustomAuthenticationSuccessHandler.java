package tpfilrouge.tp_fil_rouge.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tpfilrouge.tp_fil_rouge.modele.entite.Utilisateur;
import tpfilrouge.tp_fil_rouge.modele.repository.UtilisateurRepository;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UtilisateurRepository utilisateurRepository;

    public CustomAuthenticationSuccessHandler(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = authentication.getName();
        Utilisateur u = utilisateurRepository.findByUsername(username).orElse(null);
        if (u != null && u.isMustChangePassword()) {
            // rediriger vers la page de setup
            response.sendRedirect(request.getContextPath() + "/user/setup");
        } else {
            // Default behaviour
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
