package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;

import java.util.Optional;

@Repository
public interface TuteurEnseignantRepository extends JpaRepository<TuteurEnseignant, Integer> {
    
    Optional<TuteurEnseignant> findByLogin(String login);
    
    boolean existsByLogin(String login);
}