package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    @Autowired
    public AnneeAcademiqueService(AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    public List<AnneeAcademique> getAllAnnees() {
        return anneeAcademiqueRepository.findAll();
    }

    public Optional<AnneeAcademique> getAnneeById(Integer id) {
        return anneeAcademiqueRepository.findById(id);
    }

    public AnneeAcademique createAnnee(AnneeAcademique annee) {
        return anneeAcademiqueRepository.save(annee);
    }

    public AnneeAcademique updateAnnee(Integer id, AnneeAcademique anneeDetails) {
        AnneeAcademique annee = anneeAcademiqueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Année académique non trouvée"));

        annee.setAnnee(anneeDetails.getAnnee());
        annee.setEstCourante(anneeDetails.getEstCourante());

        return anneeAcademiqueRepository.save(annee);
    }

    public void deleteAnnee(Integer id) {
        anneeAcademiqueRepository.deleteById(id);
    }

    public Optional<AnneeAcademique> getAnneeCourante() {
        return anneeAcademiqueRepository.findByEstCouranteTrue();
    }

    @Transactional
    public AnneeAcademique definirNouvelleAnneeCourante(Integer nouvelleAnneeId) {
        // Désactiver l'année courante actuelle
        anneeAcademiqueRepository.findByEstCouranteTrue()
            .ifPresent(annee -> {
                annee.setEstCourante(false);
                anneeAcademiqueRepository.save(annee);
            });

        // Activer la nouvelle année
        AnneeAcademique nouvelleAnnee = anneeAcademiqueRepository.findById(nouvelleAnneeId)
            .orElseThrow(() -> new RuntimeException("Nouvelle année académique non trouvée"));
        nouvelleAnnee.setEstCourante(true);

        return anneeAcademiqueRepository.save(nouvelleAnnee);
    }
}

