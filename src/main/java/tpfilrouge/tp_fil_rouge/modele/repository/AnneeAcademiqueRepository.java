package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;

import java.util.Optional;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<AnneeAcademique, Integer> {
    
    Optional<AnneeAcademique> findByEstCouranteTrue();
    
    boolean existsByAnnee(String annee);
    
    @Modifying
    @Query("UPDATE AnneeAcademique a SET a.estCourante = false WHERE a.estCourante = true")
    void desactiverToutesAnneesCourantes();


}