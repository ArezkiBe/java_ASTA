package tpfilrouge.tp_fil_rouge.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Implémentation personnalisée de UserDetails pour gérer les informations supplémentaires
 * comme le changement obligatoire d'identifiants
 */
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean mustChangeCredentials;
    private final String prenom;
    private final Integer tuteurId;

    public CustomUserDetails(String username, String password, 
                           Collection<? extends GrantedAuthority> authorities,
                           boolean mustChangeCredentials, String prenom, Integer tuteurId) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.mustChangeCredentials = mustChangeCredentials;
        this.prenom = prenom;
        this.tuteurId = tuteurId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Méthodes personnalisées
    public boolean mustChangeCredentials() {
        return mustChangeCredentials;
    }

    public String getPrenom() {
        return prenom;
    }

    public Integer getTuteurId() {
        return tuteurId;
    }
}