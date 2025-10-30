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
import tpfilrouge.tp_fil_rouge.exceptions.MaitreApprentissageNonTrouveException;
import tpfilrouge.tp_fil_rouge.modele.entite.MaitreApprentissage;
import tpfilrouge.tp_fil_rouge.services.MaitreApprentissageService;

import java.util.List;

/**
 * Contrôleur web pour la gestion des maîtres d'apprentissage via interface Thymeleaf
 * 
 * Fonctionnalités :
 * - Liste des maîtres d'apprentissage
 * - Détails d'un maître d'apprentissage
 * - Ajout de nouveaux maîtres
 * - Modification des maîtres existants
 * - Suppression des maîtres
 * 
 * @author Système ASTA
 * @version 1.0
 */
@Controller
@RequestMapping("/web/maitres")
public class WebMaitreApprentissageController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebMaitreApprentissageController.class);
    
    // Messages constants pour éviter la duplication
    private static final String MESSAGE_MODIFICATION_REUSSIE = "Maître d'apprentissage modifié avec succès !";
    private static final String MESSAGE_MODIFICATION_ECHEC = "Échec de la modification";
    private static final String MESSAGE_AJOUT_REUSSI = "Maître d'apprentissage ajouté avec succès !";
    private static final String MESSAGE_AJOUT_ECHEC = "Échec de l'ajout";
    private static final String MESSAGE_SUPPRESSION_REUSSIE = "Maître d'apprentissage supprimé avec succès !";
    
    // Attributs de modèle constants
    private static final String ATTR_MAITRES = "maitres";
    private static final String ATTR_MAITRE = "maitre";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_ERREUR = "erreur";
    
    // Vues constants
    private static final String VUE_LISTE = "maitres/liste";
    private static final String VUE_DETAILS = "maitres/details";
    private static final String VUE_FORMULAIRE = "maitres/formulaire";
    private static final String REDIRECT_LISTE = "redirect:/web/maitres";
    
    private final MaitreApprentissageService maitreApprentissageService;
    
    @Autowired
    public WebMaitreApprentissageController(MaitreApprentissageService maitreApprentissageService) {
        this.maitreApprentissageService = maitreApprentissageService;
    }
    
    /**
     * Affiche la liste des maîtres d'apprentissage
     */
    @GetMapping
    public String afficherListeMaitres(Model model) {
        logger.info("Affichage de la liste des maîtres d'apprentissage");
        
        try {
            List<MaitreApprentissage> maitres = maitreApprentissageService.getAllMaitres();
            model.addAttribute(ATTR_MAITRES, maitres);
            
            // Statistiques pour l'affichage
            model.addAttribute("nombreMaitres", maitres.size());
            
            logger.info("Liste des maîtres chargée avec succès : {} maîtres trouvés", maitres.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la liste des maîtres", e);
            model.addAttribute(ATTR_ERREUR, "Erreur lors du chargement des données");
        }
        
        return VUE_LISTE;
    }
    
    /**
     * Affiche les détails d'un maître d'apprentissage
     */
    @GetMapping("/{id}")
    public String afficherDetailsMaitre(@PathVariable Integer id, Model model) {
        logger.info("Affichage des détails du maître ID: {}", id);
        
        try {
            MaitreApprentissage maitre = maitreApprentissageService.getMaitreById(id)
                .orElseThrow(() -> new MaitreApprentissageNonTrouveException("Maître d'apprentissage non trouvé avec l'ID: " + id));
            
            model.addAttribute(ATTR_MAITRE, maitre);
            
            logger.info("Détails du maître {} {} chargés avec succès", maitre.getPrenom(), maitre.getNom());
            
        } catch (MaitreApprentissageNonTrouveException e) {
            logger.warn("Maître non trouvé ID: {}", id);
            model.addAttribute(ATTR_ERREUR, "Maître d'apprentissage non trouvé");
            return VUE_LISTE;
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des détails du maître ID: {}", id, e);
            model.addAttribute(ATTR_ERREUR, "Erreur lors du chargement des détails");
            return VUE_LISTE;
        }
        
        return VUE_DETAILS;
    }
    
    /**
     * Affiche le formulaire d'ajout d'un nouveau maître
     */
    @GetMapping("/ajouter")
    public String afficherFormulaireAjout(Model model) {
        logger.info("Affichage du formulaire d'ajout de maître");
        
        model.addAttribute(ATTR_MAITRE, new MaitreApprentissage());
        model.addAttribute("modeModification", false);
        
        return VUE_FORMULAIRE;
    }
    
    /**
     * Traite l'ajout d'un nouveau maître d'apprentissage
     */
    @PostMapping("/ajouter")
    public String traiterAjoutMaitre(@Valid @ModelAttribute(ATTR_MAITRE) MaitreApprentissage maitre,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
        logger.info("Traitement de l'ajout d'un nouveau maître: {} {}", maitre.getPrenom(), maitre.getNom());
        
        // Validation des erreurs
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de l'ajout du maître {} {}", maitre.getPrenom(), maitre.getNom());
            
            model.addAttribute("modeModification", false);
            model.addAttribute(ATTR_ERREUR, "Veuillez corriger les erreurs dans le formulaire");
            
            return VUE_FORMULAIRE;
        }
        
        try {
            // Créer le maître
            MaitreApprentissage nouveauMaitre = maitreApprentissageService.createMaitre(maitre);
            
            logger.info("Ajout réussi pour le maître {} {}", nouveauMaitre.getPrenom(), nouveauMaitre.getNom());
            redirectAttributes.addFlashAttribute(ATTR_MESSAGE, MESSAGE_AJOUT_REUSSI);
            
            return "redirect:/web/maitres/" + nouveauMaitre.getId();
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du maître {} {}", maitre.getPrenom(), maitre.getNom(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, MESSAGE_AJOUT_ECHEC);
            return "redirect:/web/maitres/ajouter";
        }
    }
    
    /**
     * Affiche le formulaire de modification d'un maître
     */
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Integer id, Model model) {
        logger.info("Affichage du formulaire de modification du maître ID: {}", id);
        
        try {
            MaitreApprentissage maitre = maitreApprentissageService.getMaitreById(id)
                .orElseThrow(() -> new MaitreApprentissageNonTrouveException("Maître d'apprentissage non trouvé"));
            
            model.addAttribute(ATTR_MAITRE, maitre);
            model.addAttribute("modeModification", true);
            
            return VUE_FORMULAIRE;
            
        } catch (MaitreApprentissageNonTrouveException e) {
            logger.warn("Maître non trouvé pour modification ID: {}", id);
            model.addAttribute(ATTR_ERREUR, "Maître d'apprentissage non trouvé");
            return VUE_LISTE;
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du formulaire de modification ID: {}", id, e);
            model.addAttribute(ATTR_ERREUR, "Erreur lors du chargement du formulaire");
            return VUE_LISTE;
        }
    }
    
    /**
     * Traite la modification d'un maître d'apprentissage
     */
    @PostMapping("/{id}/modifier")
    public String traiterModificationMaitre(@PathVariable Integer id,
                                           @Valid @ModelAttribute(ATTR_MAITRE) MaitreApprentissage maitre,
                                           BindingResult result,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        
        logger.info("Traitement de la modification du maître ID: {}", id);
        
        // Validation des erreurs
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de la modification du maître ID: {}", id);
            
            model.addAttribute("modeModification", true);
            model.addAttribute(ATTR_ERREUR, "Veuillez corriger les erreurs dans le formulaire");
            
            return VUE_FORMULAIRE;
        }
        
        try {
            // S'assurer que l'ID est correct
            maitre.setId(id);
            
            MaitreApprentissage maitreModifie = maitreApprentissageService.updateMaitre(id, maitre);
            
            logger.info("Modification réussie pour le maître ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_MESSAGE, MESSAGE_MODIFICATION_REUSSIE);
            
            return "redirect:/web/maitres/" + maitreModifie.getId();
            
        } catch (MaitreApprentissageNonTrouveException e) {
            logger.warn("Maître non trouvé pour modification ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Maître d'apprentissage non trouvé");
            return REDIRECT_LISTE;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la modification du maître ID: {}", id, e);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, MESSAGE_MODIFICATION_ECHEC);
            return "redirect:/web/maitres/" + id + "/modifier";
        }
    }
    
    /**
     * Suppression d'un maître d'apprentissage
     */
    @PostMapping("/{id}/supprimer")
    public String supprimerMaitre(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Suppression du maître ID: {}", id);
        
        try {
            maitreApprentissageService.deleteMaitre(id);
            
            logger.info("Maître ID: {} supprimé avec succès", id);
            redirectAttributes.addFlashAttribute(ATTR_MESSAGE, MESSAGE_SUPPRESSION_REUSSIE);
            
        } catch (MaitreApprentissageNonTrouveException e) {
            logger.warn("Tentative de suppression d'un maître inexistant ID: {}", id);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, "Maître d'apprentissage non trouvé");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du maître ID: {}", id, e);
            redirectAttributes.addFlashAttribute(ATTR_ERREUR, e.getMessage());
        }
        
        return REDIRECT_LISTE;
    }
}