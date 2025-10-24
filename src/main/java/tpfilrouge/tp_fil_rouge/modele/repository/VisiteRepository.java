package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;

import java.util.List;

@Repository
public interface VisiteRepository extends JpaRepository<Visite, Integer> {

    // Méthodes de recherche basiques - les relations avec apprenti et tuteur n'existent pas dans cette entité
    List<Visite> findByFormatOrderByDateDesc(String format);
}

