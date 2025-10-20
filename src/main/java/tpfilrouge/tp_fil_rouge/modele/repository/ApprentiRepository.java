package tpfilrouge.tp_fil_rouge.modele.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;

import java.util.List;

@Repository
public interface ApprentiRepository extends JpaRepository<Apprenti, Integer> {
    
    List<Apprenti> findByEstArchiveFalseOrderByNomAscPrenomAsc();
    
    List<Apprenti> findByTuteurEnseignantIdAndEstArchiveFalse(Integer tuteurId);
    
    List<Apprenti> findByAnneeAcademiqueIdAndEstArchiveFalse(Integer anneeId);
    
    boolean existsByEmail(String email);
    
    @Query(value = "SELECT COUNT(*) FROM apprenti WHERE est_archive = 0", nativeQuery = true)
    Long countApprentisCourantsSQL();
}