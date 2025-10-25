package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/session/check")
    public ResponseEntity<?> check(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("NOT_AUTHENTICATED");
        }
        return ResponseEntity.ok("OK");
    }
}

