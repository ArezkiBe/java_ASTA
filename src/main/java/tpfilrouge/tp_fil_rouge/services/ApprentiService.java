package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tpfilrouge.tp_fil_rouge.exceptions.ApprentiNonTrouveException;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.repository.ApprentiRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;

import java.util.List;

@Service
public class ApprentiService {
    private final ApprentiRepository apprentiRepository;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;

    @Autowired
    public ApprentiService(ApprentiRepository apprentiRepository, AnneeAcademiqueRepository anneeAcademiqueRepository) {
        this.apprentiRepository = apprentiRepository;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
    }

    // CRUD
    public Apprenti createApprenti(Apprenti apprenti) {
        return apprentiRepository.save(apprenti);
    }

    public Apprenti getApprentiById(Integer id) {
        return apprentiRepository.findById(id)
            .orElseThrow(() -> new ApprentiNonTrouveException(id));
    }

    public List<Apprenti> getAllApprentis() {
        return apprentiRepository.findAll();
    }

    public Apprenti updateApprenti(Integer id, Apprenti apprentiDetails) {
        Apprenti apprenti = getApprentiById(id);
        apprenti.setNom(apprentiDetails.getNom());
        apprenti.setPrenom(apprentiDetails.getPrenom());
        apprenti.setEmail(apprentiDetails.getEmail());
        apprenti.setTelephone(apprentiDetails.getTelephone());
        apprenti.setProgramme(apprentiDetails.getProgramme());
        apprenti.setMajeure(apprentiDetails.getMajeure());
        apprenti.setEntreprise(apprentiDetails.getEntreprise());
        apprenti.setTuteurEnseignant(apprentiDetails.getTuteurEnseignant());
        apprenti.setAnneeAcademique(apprentiDetails.getAnneeAcademique());
        apprenti.setMaitreApprentissage(apprentiDetails.getMaitreApprentissage());
        apprenti.setMission(apprentiDetails.getMission());
        apprenti.setFeedbackTuteurEnseignant(apprentiDetails.getFeedbackTuteurEnseignant());
        apprenti.setEstArchive(apprentiDetails.getEstArchive());
        return apprentiRepository.save(apprenti);
    }

    public void deleteApprenti(Integer id) {
        apprentiRepository.deleteById(id);
    }

    // Règle métier : promotion et archivage pour nouvelle année académique
    @Transactional
    public String promouvoirEtArchiverApprentisNouvelleAnnee(Integer nouvelleAnneeId) {
        // Vérifier qu'une année courante existe
        AnneeAcademique anneeCourante = anneeAcademiqueRepository.findByEstCouranteTrue()
            .orElseThrow(() -> new RuntimeException("Aucune année académique courante trouvée"));

        // Vérifier que la nouvelle année existe
        AnneeAcademique nouvelleAnnee = anneeAcademiqueRepository.findById(nouvelleAnneeId)
            .orElseThrow(() -> new RuntimeException("Nouvelle année académique non trouvée"));

        // Récupérer tous les apprentis de l'année courante non archivés
        List<Apprenti> apprentisCourants = apprentiRepository.findByAnneeAcademiqueIdAndEstArchiveFalse(anneeCourante.getId());

        if (apprentisCourants.isEmpty()) {
            return "Aucun apprenti à promouvoir pour l'année " + anneeCourante.getAnnee();
        }

        int promusI1I2 = 0, promusI2I3 = 0, archivesI3 = 0, autresProgrammes = 0;

        // Appliquer les règles de promotion/archivage
        for (Apprenti apprenti : apprentisCourants) {
            String programme = apprenti.getProgramme();

            switch (programme) {
                case "I1":
                    apprenti.setProgramme("I2");
                    apprenti.setAnneeAcademique(nouvelleAnnee);
                    promusI1I2++;
                    break;
                case "I2":
                    apprenti.setProgramme("I3");
                    apprenti.setAnneeAcademique(nouvelleAnnee);
                    promusI2I3++;
                    break;
                case "I3":
                    apprenti.setEstArchive(true);
                    // Les I3 restent dans l'ancienne année mais sont archivés
                    archivesI3++;
                    break;
                default:
                    // Autres programmes (M2-PRO, etc.) : simple changement d'année
                    apprenti.setAnneeAcademique(nouvelleAnnee);
                    autresProgrammes++;
                    break;
            }
        }

        // Sauvegarder tous les apprentis modifiés en une seule fois (optimisation)
        apprentiRepository.saveAll(apprentisCourants);

        // Créer un rapport de la promotion
        return String.format(
            "Promotion terminée : %d I1→I2, %d I2→I3, %d I3 archivés, %d autres programmes transférés",
            promusI1I2, promusI2I3, archivesI3, autresProgrammes
        );
    }

    // Méthode simplifiée pour promouvoir sans changer d'année (rétrocompatibilité)
    @Transactional
    public void promouvoirEtArchiverApprentisNouvelleAnnee() {
        AnneeAcademique anneeCourante = anneeAcademiqueRepository.findByEstCouranteTrue()
            .orElseThrow(() -> new RuntimeException("Année académique courante non trouvée"));

        promouvoirEtArchiverApprentisNouvelleAnnee(anneeCourante.getId());
    }
}

