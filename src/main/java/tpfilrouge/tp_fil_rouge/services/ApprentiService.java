package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tpfilrouge.tp_fil_rouge.exceptions.ApprentiNonTrouveException;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.modele.entite.MaitreApprentissage;
import tpfilrouge.tp_fil_rouge.modele.repository.ApprentiRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.TuteurEnseignantRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.MaitreApprentissageRepository;

import java.util.List;

@Service
public class ApprentiService {
    private final ApprentiRepository apprentiRepository;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final TuteurEnseignantRepository tuteurEnseignantRepository;
    private final MaitreApprentissageRepository maitreApprentissageRepository;

    @Autowired
    public ApprentiService(ApprentiRepository apprentiRepository, 
                          AnneeAcademiqueRepository anneeAcademiqueRepository,
                          TuteurEnseignantRepository tuteurEnseignantRepository,
                          MaitreApprentissageRepository maitreApprentissageRepository) {
        this.apprentiRepository = apprentiRepository;
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.tuteurEnseignantRepository = tuteurEnseignantRepository;
        this.maitreApprentissageRepository = maitreApprentissageRepository;
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
        // Vérifier si l'apprenti existe
        if (!apprentiRepository.existsById(id)) {
            throw new RuntimeException("Apprenti non trouvé avec l'ID : " + id);
        }
        
        // Vérifier s'il y a des dépendances
        Apprenti apprenti = apprentiRepository.findById(id).get();
        
        // Vérifier s'il y a des évaluations associées
        if (!apprenti.getEvaluations().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer cet apprenti : il a " + 
                apprenti.getEvaluations().size() + " évaluation(s) associée(s). " +
                "Veuillez d'abord supprimer les évaluations.");
        }
        
        // Vérifier s'il y a des visites associées
        if (!apprenti.getVisites().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer cet apprenti : il a " + 
                apprenti.getVisites().size() + " visite(s) associée(s). " +
                "Veuillez d'abord supprimer les visites.");
        }
        
        apprentiRepository.deleteById(id);
    }

    public String importerApprentisCsv(String csvContent) {
        if (csvContent == null || csvContent.trim().isEmpty()) {
            throw new RuntimeException("Le fichier CSV est vide");
        }
        
        String[] lignes = csvContent.split("\\r?\\n");
        int apprentisCreés = 0;
        int lignesIgnorées = 0;
        StringBuilder rapport = new StringBuilder();
        
        // Vérifier les prérequis
        AnneeAcademique anneeCourante = anneeAcademiqueRepository.findByEstCouranteTrue()
            .orElse(null);
        if (anneeCourante == null) {
            return "Erreur : Aucune année académique courante définie.";
        }
        
        TuteurEnseignant tuteurParDefaut = tuteurEnseignantRepository.findAll().stream()
            .findFirst().orElse(null);
        if (tuteurParDefaut == null) {
            return "Erreur : Aucun tuteur enseignant disponible.";
        }
        
        MaitreApprentissage maitreParDefaut = maitreApprentissageRepository.findAll().stream()
            .findFirst().orElse(null);
        if (maitreParDefaut == null) {
            return "Erreur : Aucun maître d'apprentissage disponible.";
        }
        
        for (int i = 0; i < lignes.length; i++) {
            String ligne = lignes[i].trim();
            
            if (ligne.isEmpty() || ligne.toLowerCase().startsWith("nom") || ligne.startsWith("#")) {
                continue;
            }
            
            String[] colonnes = ligne.split(",");
            if (colonnes.length < 3) {
                lignesIgnorées++;
                rapport.append("Ligne ").append(i + 1).append(" ignorée (format incorrect)\n");
                continue;
            }
            
            try {
                String nom = colonnes[0].trim();
                String prenom = colonnes[1].trim();
                String email = colonnes[2].trim();
                
                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                    lignesIgnorées++;
                    rapport.append("Ligne ").append(i + 1).append(" ignorée (champs vides)\n");
                    continue;
                }
                
                if (apprentiRepository.findByEmail(email).isPresent()) {
                    lignesIgnorées++;
                    rapport.append("Ligne ").append(i + 1).append(" ignorée (email existant : ").append(email).append(")\n");
                    continue;
                }
                
                Apprenti nouvelApprenti = new Apprenti();
                nouvelApprenti.setNom(nom);
                nouvelApprenti.setPrenom(prenom);
                nouvelApprenti.setEmail(email);
                nouvelApprenti.setProgramme("I1");
                nouvelApprenti.setMajeure("Digital Transformation");
                nouvelApprenti.setEstArchive(false);
                nouvelApprenti.setAnneeAcademique(anneeCourante);
                nouvelApprenti.setTuteurEnseignant(tuteurParDefaut);
                nouvelApprenti.setMaitreApprentissage(maitreParDefaut);
                
                apprentiRepository.save(nouvelApprenti);
                apprentisCreés++;
                
            } catch (Exception e) {
                lignesIgnorées++;
                rapport.append("Ligne ").append(i + 1).append(" ignorée (erreur : ").append(e.getMessage()).append(")\n");
            }
        }
        
        String resultat = String.format("Import terminé : %d apprenti(s) créé(s), %d ligne(s) ignorée(s)",
                                       apprentisCreés, lignesIgnorées);
        
        return rapport.length() > 0 ? resultat + "\n\nDétails :\n" + rapport : resultat;
    }

    public List<Apprenti> getApprentisCourants() {
        return apprentiRepository.findByEstArchiveFalseOrderByNomAscPrenomAsc();
    }
    
    public List<Apprenti> getApprentisAnneeCourante() {
        return anneeAcademiqueRepository.findByEstCouranteTrue()
            .map(anneeCourante -> apprentiRepository.findByAnneeAcademiqueIdAndEstArchiveFalse(anneeCourante.getId()))
            .orElse(java.util.Collections.emptyList());
    }

    public boolean existsByEmail(String email) {
        return apprentiRepository.existsByEmail(email);
    }

    @Transactional
    public String promouvoirEtArchiverApprentisNouvelleAnnee(Integer nouvelleAnneeId) {
        AnneeAcademique anneeCourante = anneeAcademiqueRepository.findByEstCouranteTrue()
            .orElseThrow(() -> new RuntimeException("Aucune année académique courante trouvée"));

        AnneeAcademique nouvelleAnnee = anneeAcademiqueRepository.findById(nouvelleAnneeId)
            .orElseThrow(() -> new RuntimeException("Nouvelle année académique non trouvée"));

        List<Apprenti> apprentisCourants = apprentiRepository.findByAnneeAcademiqueIdAndEstArchiveFalse(anneeCourante.getId());

        if (apprentisCourants.isEmpty()) {
            return "Aucun apprenti à promouvoir pour l'année " + anneeCourante.getAnnee();
        }

        int promusI1I2 = 0, promusI2I3 = 0, archivesI3 = 0, autresProgrammes = 0;
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
                    archivesI3++;
                    break;
                default:
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

    public long compterApprentisByProgramme(String programme) {
        return apprentiRepository.countByProgrammeAndEstArchiveFalse(programme);
    }

    public List<Apprenti> rechercherParNom(String nom) {
        return apprentiRepository.findByNomContainingIgnoreCaseAndEstArchiveFalse(nom);
    }

    public List<Apprenti> rechercherParEntreprise(Integer entrepriseId) {
        return apprentiRepository.findByEntrepriseIdAndEstArchiveFalse(entrepriseId);
    }

    public List<Apprenti> rechercherParAnnee(String annee) {
        return apprentiRepository.findByAnneeAcademiqueAnneeAndEstArchiveFalse(annee);
    }

    public List<Apprenti> rechercherParMission(String motCle) {
        return apprentiRepository.findByMissionMotsClesContainingAndEstArchiveFalse(motCle);
    }
    
    public List<Apprenti> getApprentisParAnnee(Integer anneeId) {
        return apprentiRepository.findByAnneeAcademiqueIdAndEstArchiveFalse(anneeId);
    }
}

