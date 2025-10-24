package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;

public interface VisiteRepository extends JpaRepository<Visite, Integer> {
}

