package tpfilrouge.tp_fil_rouge.controleur.web;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tpfilrouge.tp_fil_rouge.exceptions.ApprentiNonTrouveException;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.TuteurEnseignant;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;
import tpfilrouge.tp_fil_rouge.services.EntrepriseService;
import tpfilrouge.tp_fil_rouge.services.AnneeAcademiqueService;
import tpfilrouge.tp_fil_rouge.services.MaitreApprentissageService;
import tpfilrouge.tp_fil_rouge.services.TuteurEnseignantService;

import java.util.List;

/**
 * Contrôleur web pour la gestion des apprentis via interface Thymeleaf
 * 
 * Conforme aux exigences fonctionnelles :
 * - Exigence #2 : Tableau de bord avec liste des apprentis
 * - Exigence #3 : Affichage des détails d'un apprenti
 * - Exigence #4 : Modification des apprentis avec validation
 * - Exigence #5 : Ajout d'apprentis
 * 
 * Respect des bonnes pratiques Clean Code :
 * - Séparation des responsabilités (web vs API REST)
 * - Gestion d'erreurs avec messages utilisateur
 * - Validation côté serveur
 * - Logging approprié
 * 
 * @author Votre nom
 * @version 1.0
 */
@Controller
@RequestMapping("/web/apprentis")
public class WebApprentiController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebApprentiController.class);
    
    // Messages constants pour éviter la duplication
    private static final String MESSAGE_MODIFICATION_REUSSIE = "Modification réussie !";
    private static final String MESSAGE_MODIFICATION_ECHEC = "Échec de la modification";
    private static final String MESSAGE_AJOUT_REUSSI = "Apprenti ajouté avec succès !";
    private static final String MESSAGE_AJOUT_ECHEC = "Échec de l'ajout";
    private static final String MESSAGE_SUPPRESSION_REUSSIE = "Apprenti supprimé avec succès !";
    
    // Attributs de modèle constants
    private static final String ATTR_APPRENTIS = "apprentis";
    private static final String ATTR_APPRENTI = "apprenti";
    private static final String ATTR_ENTREPRISES = "entreprises";
    private static final String ATTR_MAITRES = "maitres";
    private static final String ATTR_ANNEES = "annees";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_ERREUR = "erreur";
    
    // Vues constants
    private static final String VUE_LISTE = "apprentis/liste";
    private static final String VUE_DETAILS = "apprentis/details";
    private static final String VUE_FORMULAIRE = "apprentis/formulaire";
    private static final String REDIRECT_LISTE = "redirect:/web/apprentis";
    
    private final ApprentiService apprentiService;
    private final EntrepriseService entrepriseService;
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final MaitreApprentissageService maitreApprentissageService;
    private final TuteurEnseignantService tuteurEnseignantService;
    
    @Autowired
    public WebApprentiController(ApprentiService apprentiService,
                                EntrepriseService entrepriseService,
                                AnneeAcademiqueService anneeAcademiqueService,
                                MaitreApprentissageService maitreApprentissageService,
                                TuteurEnseignantService tuteurEnseignantService) {
        this.apprentiService = apprentiService;
        this.entrepriseService = entrepriseService;
        this.anneeAcademiqueService = anneeAcademiqueService;
        this.maitreApprentissageService = maitreApprentissageService;
        this.tuteurEnseignantService = tuteurEnseignantService;
    }
    
    /**
     * Exigence #2 : Affiche le tableau de bord avec la liste des apprentis
     * Message "La liste est vide. Ajoutez au moins un apprenti!" si aucun apprenti
     */
    @GetMapping
    public String afficherListeApprentis(Model model) {
        logger.info("Affichage de la liste des apprentis non archivés");
        
        try {
            List<Apprenti> apprentis = apprentiService.getApprentisCourants();
            model.addAttribute(ATTR_APPRENTIS, apprentis);
            
            // Statistiques pour l'affichage
            model.addAttribute("nombreApprentis", apprentis.size());
            
            logger.info("Liste des apprentis chargée avec succès : {} apprentis trouvés", apprentis.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la liste des apprentis", e);
            model.addAttribute(ATTR_ERREUR, "Erreur lors du chargement de la liste des apprentis");
        }
        
        return VUE_LISTE;
    }
    
    /**
     * Exigence #3 : Affiche toutes les informations d'un apprenti sur une page dédiée
     */
    @GetMapping("/{id}")
    public String afficherDetailsApprenti(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Affichage des détails de l'apprenti ID: {}", id);
        
        try {
            Apprenti apprenti = apprentiService.getApprentiById(id);
            model.addAttribute(ATTR_APPRENTI, apprenti);
            
            logger.info("Détails de l'apprenti {} {} chargés avec succès", apprenti.getPrenom(), apprenti.getNom());
            return VUE_DETAILS;
            
        } catch (ApprentiNonTrouveException e) {
            logger.warn("Apprenti non trouvé avec l'ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Apprenti non trouvé");
            return REDIRECT_LISTE;
        }
    }
    
    /**
     * Exigence #4 : Formulaire de modification d'un apprenti (GET)
     */
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Affichage du formulaire de modification pour l'apprenti ID: {}", id);
        
        try {
            Apprenti apprenti = apprentiService.getApprentiById(id);
            model.addAttribute(ATTR_APPRENTI, apprenti);
            
            // Charger les données nécessaires pour les listes déroulantes
            model.addAttribute(ATTR_ENTREPRISES, entrepriseService.getAllEntreprises());
            model.addAttribute(ATTR_MAITRES, maitreApprentissageService.getAllMaitres());
            model.addAttribute(ATTR_ANNEES, anneeAcademiqueService.getAllAnnees());
            
            model.addAttribute("modeModification", true);
            
            return VUE_FORMULAIRE;
            
        } catch (ApprentiNonTrouveException e) {
            logger.warn("Tentative de modification d'un apprenti inexistant ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Apprenti non trouvé pour modification");
            return REDIRECT_LISTE;
        }
    }
    
    /**
     * Exigence #4 : Traitement de la modification d'un apprenti (POST)
     * Messages "Modification réussie !" ou "Échec de la modification"
     */
    @PostMapping("/{id}/modifier")
    public String traiterModificationApprenti(@PathVariable Integer id,
                                            @Valid @ModelAttribute(ATTR_APPRENTI) Apprenti apprenti,
                                            BindingResult result,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        
        logger.info("Traitement de la modification de l'apprenti ID: {}", id);
        
        // Validation des erreurs
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de la modification de l'apprenti ID: {}", id);
            
            // Recharger les données pour le formulaire
            model.addAttribute(ATTR_ENTREPRISES, entrepriseService.getAllEntreprises());
            model.addAttribute(ATTR_MAITRES, maitreApprentissageService.getAllMaitres());
            model.addAttribute(ATTR_ANNEES, anneeAcademiqueService.getAllAnnees());
            model.addAttribute("modeModification", true);
            model.addAttribute(ATTR_ERREUR, "Veuillez corriger les erreurs dans le formulaire");
            
            return VUE_FORMULAIRE;
        }
        
        try {
            // Appliquer la modification
            Apprenti apprentiModifie = apprentiService.updateApprenti(id, apprenti);
            
            logger.info("Modification réussie pour l'apprenti {} {}", apprentiModifie.getPrenom(), apprentiModifie.getNom());
            redirectAttributes.addFlashAttribute(ATTR_MESSAGE, MESSAGE_MODIFICATION_REUSSIE);
            
            return "redirect:/web/apprentis/" + id;
            
        } catch (ApprentiNonTrouveException e) {
            logger.error("Apprenti non trouvé lors de la modification ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Apprenti non trouvé");
            return REDIRECT_LISTE;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de l'apprenti ID: {}", id, e);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, MESSAGE_MODIFICATION_ECHEC);
            return "redirect:/web/apprentis/" + id + "/modifier";
        }
    }
    
    /**
     * Exigence #5 : Formulaire d'ajout d'un nouvel apprenti (GET)
     */
    @GetMapping("/ajouter")
    public String afficherFormulaireAjout(Model model) {
        logger.info("Affichage du formulaire d'ajout d'un nouvel apprenti");
        
        // Créer un apprenti vide pour le formulaire
        Apprenti nouvelApprenti = new Apprenti();
        model.addAttribute(ATTR_APPRENTI, nouvelApprenti);
        
        // Charger les données nécessaires
        model.addAttribute(ATTR_ENTREPRISES, entrepriseService.getAllEntreprises());
        model.addAttribute(ATTR_MAITRES, maitreApprentissageService.getAllMaitres());
        model.addAttribute(ATTR_ANNEES, anneeAcademiqueService.getAllAnnees());
        
        model.addAttribute("modeModification", false);
        
        return VUE_FORMULAIRE;
    }
    
    /**
     * Exigence #5 : Traitement de l'ajout d'un nouvel apprenti (POST)
     */
    @PostMapping("/ajouter")
    public String traiterAjoutApprenti(@Valid @ModelAttribute(ATTR_APPRENTI) Apprenti apprenti,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        
        logger.info("Traitement de l'ajout d'un nouvel apprenti: {} {}", apprenti.getPrenom(), apprenti.getNom());
        
        // Validation des erreurs
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de l'ajout de l'apprenti {} {}", apprenti.getPrenom(), apprenti.getNom());
            
            model.addAttribute(ATTR_ENTREPRISES, entrepriseService.getAllEntreprises());
            model.addAttribute(ATTR_MAITRES, maitreApprentissageService.getAllMaitres());
            model.addAttribute(ATTR_ANNEES, anneeAcademiqueService.getAllAnnees());
            model.addAttribute("modeModification", false);
            model.addAttribute(ATTR_ERREUR, "Veuillez corriger les erreurs dans le formulaire");
            
            return VUE_FORMULAIRE;
        }
        
        try {
            // Vérifier si l'email existe déjà
            if (apprentiService.existsByEmail(apprenti.getEmail())) {
                logger.warn("Tentative d'ajout d'un apprenti avec un email déjà existant: {}", apprenti.getEmail());
                
                model.addAttribute(ATTR_ENTREPRISES, entrepriseService.getAllEntreprises());
                model.addAttribute(ATTR_MAITRES, maitreApprentissageService.getAllMaitres());
                model.addAttribute(ATTR_ANNEES, anneeAcademiqueService.getAllAnnees());
                model.addAttribute("modeModification", false);
                model.addAttribute(ATTR_ERREUR, "Un apprenti avec cet email existe déjà");
                
                return VUE_FORMULAIRE;
            }
            
            // Auto-assigner le tuteur enseignant connecté
            TuteurEnseignant tuteurConnecte = getTuteurConnecte();
            if (tuteurConnecte == null) {
                logger.error("Impossible de trouver le tuteur enseignant connecté");
                model.addAttribute(ATTR_ENTREPRISES, entrepriseService.getAllEntreprises());
                model.addAttribute(ATTR_MAITRES, maitreApprentissageService.getAllMaitres());
                model.addAttribute(ATTR_ANNEES, anneeAcademiqueService.getAllAnnees());
                model.addAttribute("modeModification", false);
                model.addAttribute(ATTR_ERREUR, "Erreur : impossible de déterminer le tuteur enseignant");
                
                return VUE_FORMULAIRE;
            }
            
            apprenti.setTuteurEnseignant(tuteurConnecte);
            logger.info("Assignation du tuteur enseignant {} {} à l'apprenti", 
                       tuteurConnecte.getPrenom(), tuteurConnecte.getNom());
            
            // Créer l'apprenti
            Apprenti nouvelApprenti = apprentiService.createApprenti(apprenti);
            
            logger.info("Ajout réussi pour l'apprenti {} {}", nouvelApprenti.getPrenom(), nouvelApprenti.getNom());
            redirectAttributes.addFlashAttribute(ATTR_MESSAGE, MESSAGE_AJOUT_REUSSI);
            
            return "redirect:/web/apprentis/" + nouvelApprenti.getId();
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de l'apprenti {} {}", apprenti.getPrenom(), apprenti.getNom(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, MESSAGE_AJOUT_ECHEC);
            return "redirect:/web/apprentis/ajouter";
        }
    }
    
    /**
     * Suppression d'un apprenti (archivage)
     */
    @PostMapping("/{id}/supprimer")
    public String supprimerApprenti(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Suppression (archivage) de l'apprenti ID: {}", id);
        
        try {
            apprentiService.deleteApprenti(id);
            
            logger.info("Apprenti ID: {} supprimé avec succès", id);
            redirectAttributes.addFlashAttribute(ATTR_MESSAGE, MESSAGE_SUPPRESSION_REUSSIE);
            
        } catch (ApprentiNonTrouveException e) {
            logger.warn("Tentative de suppression d'un apprenti inexistant ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Apprenti non trouvé");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'apprenti ID: {}", id, e);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Erreur lors de la suppression");
        }
        
        return REDIRECT_LISTE;
    }
    
    /**
     * Récupère le tuteur enseignant connecté depuis le contexte de sécurité Spring
     * @return TuteurEnseignant connecté ou null si non trouvé
     */
    private TuteurEnseignant getTuteurConnecte() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String loginUtilisateur = authentication.getName();
                logger.debug("Recherche du tuteur enseignant avec login: {}", loginUtilisateur);
                
                return tuteurEnseignantService.getTuteurByLogin(loginUtilisateur).orElse(null);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du tuteur enseignant connecté", e);
        }
        return null;
    }
    
}