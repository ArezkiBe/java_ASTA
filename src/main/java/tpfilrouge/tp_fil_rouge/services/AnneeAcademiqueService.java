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
        // Vérifier si l'année académique existe
        if (!anneeAcademiqueRepository.existsById(id)) {
            throw new RuntimeException("Année académique non trouvée avec l'ID : " + id);
        }
        
        AnneeAcademique annee = anneeAcademiqueRepository.findById(id).get();
        
        // Empêcher la suppression de l'année courante
        if (annee.getEstCourante()) {
            throw new RuntimeException("Impossible de supprimer l'année académique courante : " + 
                annee.getAnnee() + ". Veuillez d'abord définir une autre année comme courante.");
        }
        
        // Vérifier s'il y a des apprentis associés (archivés ou non)
        List<Apprenti> apprentisAssocies = apprentiRepository.findByAnneeAcademique(annee);
        if (!apprentisAssocies.isEmpty()) {
            throw new RuntimeException("Impossible de supprimer cette année académique : elle contient " + 
                apprentisAssocies.size() + " apprenti(s) associé(s). " +
                "Veuillez d'abord réassigner ou supprimer les apprentis.");
        }
        
        anneeAcademiqueRepository.deleteById(id);
    }

    public Optional<AnneeAcademique> getAnneeCourante() {
        return anneeAcademiqueRepository.findByEstCouranteTrue();
    }
    
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
        if (anneeCouranteOpt.isEmpty()) {
            AnneeAcademique premiere = creerNouvelleAnnee(nouvelleAnnee);
            return definirAnneeCouranteInterne(premiere.getId());
        }
        
        AnneeAcademique anneeCourante = anneeCouranteOpt.get();
        
        // 2. VALIDATION SÉCURISÉE : Vérifier que la transition est valide
        if (!estTransitionValide(anneeCourante.getAnnee(), nouvelleAnnee)) {
            throw new RuntimeException("Transition non autorisée de " + anneeCourante.getAnnee() + 
                " vers " + nouvelleAnnee + ". Seule l'année suivante immédiate est autorisée.");
        }
        
        // 3. Créer la nouvelle année si elle n'existe pas et la définir comme courante
        AnneeAcademique prochaine;
        if (!existsByAnnee(nouvelleAnnee)) {
            prochaine = creerNouvelleAnnee(nouvelleAnnee);
        } else {
            prochaine = anneeAcademiqueRepository.findByAnnee(nouvelleAnnee)
                .orElseThrow(() -> new RuntimeException("Erreur lors de la récupération de l'année"));
        }
        
        // 4. AVANT de changer l'année courante, promouvoir les apprentis
        promouvoirTousLesApprentis(prochaine, anneeCourante);
        
        // 5. PUIS définir cette année comme courante
        AnneeAcademique nouvelleCourante = definirAnneeCouranteInterne(prochaine.getId());
        
        return nouvelleCourante;
    }
    
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
     * Promeut automatiquement tous les apprentis selon les règles suivantes:
     * - L1 → L2 (avec nouvelle année académique)
     * - L2 → L3 (avec nouvelle année académique)  
     * - L3 → Archivés (gardent leur année académique de diplomation)
     * - Autres programmes → Changent seulement d'année académique (gardent leur programme)
     * 
     * Utilise des requêtes UPDATE directes pour éviter les conflits de transaction
     */
    public void promouvoirTousLesApprentis(AnneeAcademique nouvelleAnnee, AnneeAcademique ancienneAnnee) {
        // 1. Promouvoir L1 → L2 avec nouvelle année académique
        apprentiRepository.updateL1ToL2(nouvelleAnnee.getId(), ancienneAnnee.getId());
        
        // 2. Promouvoir L2 → L3 avec nouvelle année académique  
        apprentiRepository.updateL2ToL3(nouvelleAnnee.getId(), ancienneAnnee.getId());
        
        // 3. Archiver L3 (ils gardent leur année académique de diplomation)
        apprentiRepository.archiveL3(ancienneAnnee.getId());
        
        // 4. Mettre à jour l'année académique pour les autres programmes (Master, Doctorat, etc.)
        apprentiRepository.updateAutresProgrammesVersNouvelleAnnee(nouvelleAnnee.getId(), ancienneAnnee.getId());
    }
}

