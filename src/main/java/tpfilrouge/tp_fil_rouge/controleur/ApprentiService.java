package tpfilrouge.tp_fil_rouge.controleur;

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
            .orElseThrow(() -> new ApprentiNonTrouveException("Apprenti non trouvé"));
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

    // Règle métier : promotion et archivage
    @Transactional
    public void promouvoirEtArchiverApprentisNouvelleAnnee() {
        AnneeAcademique anneeCourante = anneeAcademiqueRepository.findByEstCouranteTrue()
            .orElseThrow(() -> new RuntimeException("Année académique courante non trouvée"));
        List<Apprenti> apprentis = apprentiRepository.findByAnneeAcademique(anneeCourante);
        for (Apprenti apprenti : apprentis) {
            String programme = apprenti.getProgramme();
            if (programme.equals("I1")) {
                apprenti.setProgramme("I2");
            } else if (programme.equals("I2")) {
                apprenti.setProgramme("I3");
            } else if (programme.equals("I3")) {
                apprenti.setEstArchive(true);
            }
            apprentiRepository.save(apprenti);
        }
    }
}
