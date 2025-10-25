package tpfilrouge.tp_fil_rouge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.PasswordResetToken;
import tpfilrouge.tp_fil_rouge.modele.entite.Utilisateur;
import tpfilrouge.tp_fil_rouge.modele.repository.PasswordResetTokenRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetService(PasswordResetTokenRepository tokenRepository, UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> createPasswordResetTokenForUser(String username) {
        Optional<Utilisateur> uOpt = utilisateurRepository.findByUsername(username);
        if (uOpt.isEmpty()) return Optional.empty();
        Utilisateur u = uOpt.get();
        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken(token, u, LocalDateTime.now().plusHours(2));
        tokenRepository.save(prt);
        return Optional.of(token);
    }

    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> prtOpt = tokenRepository.findByToken(token);
        if (prtOpt.isEmpty()) return false;
        PasswordResetToken prt = prtOpt.get();
        return prt.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public boolean resetPassword(String token, String rawPassword) {
        Optional<PasswordResetToken> prtOpt = tokenRepository.findByToken(token);
        if (prtOpt.isEmpty()) return false;
        PasswordResetToken prt = prtOpt.get();
        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(prt);
            return false;
        }
        Utilisateur u = prt.getUtilisateur();
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setMustChangePassword(false);
        utilisateurRepository.save(u);
        tokenRepository.delete(prt);
        return true;
    }
}

