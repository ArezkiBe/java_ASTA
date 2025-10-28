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
    
    // Convertie en JPQL pour respecter la règle "une seule requête SQL native"
    @Query("SELECT COUNT(a) FROM Apprenti a WHERE a.estArchive = false")
    Long countApprentisCourantsJPQL();

    List<Apprenti> findByAnneeAcademique(tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique anneeAcademique);

    // Requête SQL native complexe obligatoire pour analyse dans le rapport
    // Cette requête analyse la répartition des apprentis par programme et année académique
    @Query(value = """
        SELECT a.programme, 
               aa.annee,
               COUNT(*) as nombre_apprentis,
               COUNT(CASE WHEN a.est_archive = 1 THEN 1 END) as nombre_archives,
               COUNT(CASE WHEN a.est_archive = 0 THEN 1 END) as nombre_actifs,
               e.raison_sociale as entreprise_principale
        FROM apprenti a
        INNER JOIN annee_academique aa ON a.annee_academique_id = aa.id
        LEFT JOIN entreprise e ON a.entreprise_id = e.id
        WHERE a.programme IN ('L1', 'L2', 'L3')
        GROUP BY a.programme, aa.annee, e.raison_sociale
        HAVING COUNT(*) >= 1
        ORDER BY aa.annee DESC, a.programme ASC, nombre_apprentis DESC
        """, nativeQuery = true)
    List<Object[]> getStatistiquesApprentisParProgrammeEtAnnee();

    // Recherche par nom (Exigence 7.1.1)
    List<Apprenti> findByNomContainingIgnoreCaseAndEstArchiveFalse(String nom);

    // Recherche par entreprise (Exigence 7.1.2)
    List<Apprenti> findByEntrepriseIdAndEstArchiveFalse(Integer entrepriseId);

    // Recherche par année académique (Exigence 7.1.4)
    List<Apprenti> findByAnneeAcademiqueAnneeAndEstArchiveFalse(String annee);

    // Recherche par mission (Exigence 7.1.3)
    @Query("SELECT a FROM Apprenti a JOIN a.mission m WHERE LOWER(m.motsCles) LIKE LOWER(CONCAT('%', :motCle, '%')) AND a.estArchive = false")
    List<Apprenti> findByMissionMotsClesContainingAndEstArchiveFalse(String motCle);
    
    // Méthodes pour la gestion académique
    Long countByProgrammeAndEstArchiveFalse(String programme);
    
    List<Apprenti> findByEstArchiveFalse();
}