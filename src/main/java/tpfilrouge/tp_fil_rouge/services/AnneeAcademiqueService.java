package tpfilrouge.tp_fil_rouge.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.repository.AnneeAcademiqueRepository;
import tpfilrouge.tp_fil_rouge.modele.repository.ApprentiRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnneeAcademiqueService {
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final ApprentiRepository apprentiRepository;

    @Autowired
    public AnneeAcademiqueService(AnneeAcademiqueRepository anneeAcademiqueRepository, ApprentiRepository apprentiRepository) {
        this.anneeAcademiqueRepository = anneeAcademiqueRepository;
        this.apprentiRepository = apprentiRepository;
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

    /**
     * MÉTHODE DÉPRÉCIÉE ET DÉSACTIVÉE
     * Ancienne méthode non sécurisée pour définir l'année courante
     */
    @Deprecated
    public AnneeAcademique definirNouvelleAnneeCourante(Integer nouvelleAnneeId) {
        throw new UnsupportedOperationException(
            "La définition manuelle de l'année courante n'est plus autorisée. " +
            "Utilisez passerAAnneeSuivante() pour une progression séquentielle sécurisée."
        );
    }

    // Gestion des années académiques (Exigence 6)
    
    /** 
     * @Transactional justifiée : Cette méthode effectue 2 opérations qui doivent être atomiques :
     * 1. Vérification d'existence (SELECT)
     * 2. Création de l'année (INSERT)
     * En cas d'erreur entre les 2, tout doit être annulé pour éviter les incohérences.
     */
    @Transactional
    public AnneeAcademique creerNouvelleAnnee(String annee) {
        if (anneeAcademiqueRepository.existsByAnnee(annee)) {
            throw new RuntimeException("L'année " + annee + " existe déjà");
        }
        AnneeAcademique nouvelleAnnee = new AnneeAcademique();
        nouvelleAnnee.setAnnee(annee);
        nouvelleAnnee.setEstCourante(false);
        return anneeAcademiqueRepository.save(nouvelleAnnee);
    }
    
    public boolean existsByAnnee(String annee) {
        return anneeAcademiqueRepository.existsByAnnee(annee);
    }
    
    /**
     * MÉTHODE DÉPRÉCIÉE ET DÉSACTIVÉE
     * La définition manuelle de l'année courante n'est plus autorisée.
     * Utilisez passerAAnneeSuivante() pour une transition sécurisée.
     */
    @Deprecated
    public AnneeAcademique definirAnneeCourante(Integer anneeId) {
        throw new UnsupportedOperationException(
            "La définition manuelle de l'année courante n'est plus autorisée. " +
            "Utilisez passerAAnneeSuivante() pour une progression séquentielle sécurisée."
        );
    }
    
    /**
     * Méthode interne pour définir l'année courante (utilisée uniquement par passerAAnneeSuivante)
     */
    @Transactional  
    private AnneeAcademique definirAnneeCouranteInterne(Integer anneeId) {
        // Désactiver toutes les années courantes
        anneeAcademiqueRepository.desactiverToutesAnneesCourantes();
        
        // Activer la nouvelle année courante
        AnneeAcademique annee = anneeAcademiqueRepository.findById(anneeId)
            .orElseThrow(() -> new RuntimeException("Année académique non trouvée"));
        annee.setEstCourante(true);
        return anneeAcademiqueRepository.save(annee);
    }
    
    /**
     * Passe à l'année académique suivante avec promotion automatique des étudiants
     * SÉCURISÉ : Vérifie que la transition est séquentielle (pas de retour en arrière, pas de saut)
     * @param nouvelleAnnee L'année suivante (ex: "2025-2026")
     * @return La nouvelle année académique courante
     */
    @Transactional
    public AnneeAcademique passerAAnneeSuivante(String nouvelleAnnee) {
        // 1. Récupérer l'année courante actuelle
        Optional<AnneeAcademique> anneeCouranteOpt = getAnneeCourante();
        
        // Cas spécial : Première année académique (aucune année courante définie)
        if (anneeCouranteOpt.isEmpty()) {            AnneeAcademique premiere = creerNouvelleAnnee(nouvelleAnnee);
            return definirAnneeCouranteInterne(premiere.getId());
        }
        
        AnneeAcademique anneeCourante = anneeCouranteOpt.get();
        
        // 2. VALIDATION SÉCURISÉE : Vérifier que la transition est valide
        if (!estTransitionValide(anneeCourante.getAnnee(), nouvelleAnnee)) {
            throw new RuntimeException("Transition non autorisée de " + anneeCourante.getAnnee() + 
                " vers " + nouvelleAnnee + ". Seule l'année suivante immédiate est autorisée.");
        }
        
        // 3. Créer la nouvelle année si elle n'existe pas
        AnneeAcademique prochaine;
        if (!existsByAnnee(nouvelleAnnee)) {
            prochaine = creerNouvelleAnnee(nouvelleAnnee);
        } else {
            prochaine = anneeAcademiqueRepository.findByAnnee(nouvelleAnnee)
                .orElseThrow(() -> new RuntimeException("Erreur lors de la récupération de l'année"));
        }
        
        // 4. Promouvoir tous les apprentis actifs
        promouvoirTousLesApprentis(prochaine);
        
        // 5. Définir cette année comme courante (utilise la méthode interne sécurisée)
        return definirAnneeCouranteInterne(prochaine.getId());
    }
    
    /**
     * Valide qu'une transition d'année est autorisée (séquentielle uniquement)
     * @param anneeActuelle L'année courante (ex: "2024-2025")  
     * @param nouvelleAnnee L'année cible (ex: "2025-2026")
     * @return true si la transition est valide
     */
    private boolean estTransitionValide(String anneeActuelle, String nouvelleAnnee) {
        try {
            // Extraire les années de début des formats "YYYY-YYYY"
            int anneeDebutActuelle = Integer.parseInt(anneeActuelle.substring(0, 4));
            int anneeDebutNouvelle = Integer.parseInt(nouvelleAnnee.substring(0, 4));
            
            // Seule l'année suivante immédiate est autorisée
            return (anneeDebutNouvelle == anneeDebutActuelle + 1);
            
        } catch (Exception e) {
            // Format d'année invalide
            return false;
        }
    }
    
    /**
     * Promeut automatiquement tous les apprentis:
     * - L1 -> L2
     * - L2 -> L3  
     * - L3 -> Archivé
     */
    @Transactional
    public void promouvoirTousLesApprentis(AnneeAcademique nouvelleAnnee) {
        // Récupérer tous les apprentis non archivés
        List<Apprenti> apprentisActifs = apprentiRepository.findByEstArchiveFalseOrderByNomAscPrenomAsc();
        
        for (Apprenti apprenti : apprentisActifs) {
            switch (apprenti.getProgramme()) {
                case "L1":
                    apprenti.setProgramme("L2");
                    apprenti.setAnneeAcademique(nouvelleAnnee);
                    break;
                case "L2":
                    apprenti.setProgramme("L3");  
                    apprenti.setAnneeAcademique(nouvelleAnnee);
                    break;
                case "L3":
                    // Les L3 sont archivés (diplômés)
                    apprenti.setEstArchive(true);
                    // Ils gardent leur année académique de diplomation
                    break;
                default:
                    // Programme inconnu, on le laisse tel quel mais on change l'année
                    apprenti.setAnneeAcademique(nouvelleAnnee);
            }
            apprentiRepository.save(apprenti);
        }
    }
}

