package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tpfilrouge.tp_fil_rouge.modele.entite.Evaluation;


@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    // Méthodes de recherche basiques - les relations avec apprenti n'existent pas dans cette entité
}

