package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tpfilrouge.tp_fil_rouge.modele.entite.Entreprise;

import java.util.List;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {
    
    List<Entreprise> findByOrderByRaisonSocialeAsc();
    
    boolean existsByRaisonSociale(String raisonSociale);
}