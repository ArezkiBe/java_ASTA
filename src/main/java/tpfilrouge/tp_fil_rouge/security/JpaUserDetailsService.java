package tpfilrouge.tp_fil_rouge.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.modele.repository.TuteurEnseignantRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final TuteurEnseignantRepository tuteurEnseignantRepository;

    @Autowired
    public JpaUserDetailsService(TuteurEnseignantRepository tuteurEnseignantRepository) {
        this.tuteurEnseignantRepository = tuteurEnseignantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TuteurEnseignant tuteur = tuteurEnseignantRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tuteur non trouvé avec le login : " + username));

        // Tous les tuteurs ont le rôle ROLE_TUTEUR par défaut
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_TUTEUR")
        );

        return new CustomUserDetails(
                tuteur.getLogin(),
                tuteur.getMotDePasse(),
                authorities,
                tuteur.isDoitChangerIdentifiants(),
                tuteur.getPrenom(),
                tuteur.getId()
        );
    }
}

