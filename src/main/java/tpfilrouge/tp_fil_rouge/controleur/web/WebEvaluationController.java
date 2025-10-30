package tpfilrouge.tp_fil_rouge.controleur.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.Evaluation;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;
import tpfilrouge.tp_fil_rouge.services.EvaluationService;

import java.util.List;

/**
 * Contrôleur web pour la gestion des évaluations
 */
@Controller
@RequestMapping("/web/evaluations")
public class WebEvaluationController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebEvaluationController.class);
    
    private final EvaluationService evaluationService;
    private final ApprentiService apprentiService;
    
    @Autowired
    public WebEvaluationController(EvaluationService evaluationService, ApprentiService apprentiService) {
        this.evaluationService = evaluationService;
        this.apprentiService = apprentiService;
    }
    
    /**
     * Affiche la liste des évaluations
     */
    @GetMapping
    public String listerEvaluations(Model model) {
        logger.info("Affichage de la liste des évaluations");
        
        try {
            List<Evaluation> evaluations = evaluationService.getAllEvaluations();
            model.addAttribute("evaluations", evaluations);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des évaluations", e);
            model.addAttribute("erreur", "Erreur lors du chargement des données");
        }
        
        return "evaluations/liste";
    }
    
    /**
     * Affiche le formulaire de création d'évaluation
     */
    @GetMapping("/nouveau")
    public String afficherFormulaireCreation(Model model) {
        logger.info("Affichage du formulaire de création d'évaluation");
        
        model.addAttribute("evaluation", new Evaluation());
        model.addAttribute("modeCreation", true);
        
        // Charger la liste des apprentis actifs
        try {
            List<Apprenti> apprentis = apprentiService.getApprentisCourants();
            model.addAttribute("apprentis", apprentis);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des apprentis", e);
            model.addAttribute("apprentis", List.of());
        }
        
        return "evaluations/formulaire";
    }
    
    /**
     * Traite la création d'une évaluation
     */
    @PostMapping
    public String creerEvaluation(@ModelAttribute Evaluation evaluation, RedirectAttributes redirectAttributes) {
        logger.info("Création d'une nouvelle évaluation");
        
        try {
            evaluationService.createEvaluation(evaluation);
            
            logger.info("Évaluation créée avec succès");
            redirectAttributes.addFlashAttribute("message", "Évaluation créée avec succès !");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évaluation", e);
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la création de l'évaluation");
        }
        
        return "redirect:/web/evaluations";
    }
    
    /**
     * Affiche le détail d'une évaluation
     */
    @GetMapping("/{id}")
    public String afficherDetailEvaluation(@PathVariable Integer id, Model model) {
        logger.info("Affichage du détail de l'évaluation {}", id);
        
        try {
            Evaluation evaluation = evaluationService.getEvaluationById(id)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));
            
            model.addAttribute("evaluation", evaluation);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'évaluation {}", id, e);
            model.addAttribute("erreur", "Évaluation non trouvée");
            return "error/404";
        }
        
        return "evaluations/details";
    }
    
    /**
     * Affiche le formulaire de modification d'évaluation
     */
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Integer id, Model model) {
        logger.info("Affichage du formulaire de modification de l'évaluation {}", id);
        
        try {
            Evaluation evaluation = evaluationService.getEvaluationById(id)
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée"));
            
            model.addAttribute("evaluation", evaluation);
            model.addAttribute("modeCreation", false);
            
            // Charger la liste des apprentis actifs
            List<Apprenti> apprentis = apprentiService.getApprentisCourants();
            model.addAttribute("apprentis", apprentis);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'évaluation {}", id, e);
            model.addAttribute("erreur", "Évaluation non trouvée");
            return "error/404";
        }
        
        return "evaluations/formulaire";
    }
    
    /**
     * Traite la modification d'une évaluation
     */
    @PostMapping("/{id}")
    public String modifierEvaluation(@PathVariable Integer id, @ModelAttribute Evaluation evaluationDetails, 
                                   RedirectAttributes redirectAttributes) {
        logger.info("Modification de l'évaluation {}", id);
        
        try {
            evaluationService.updateEvaluation(id, evaluationDetails);
            
            logger.info("Évaluation {} modifiée avec succès", id);
            redirectAttributes.addFlashAttribute("message", "Évaluation modifiée avec succès !");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de l'évaluation {}", id, e);
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la modification de l'évaluation");
        }
        
        return "redirect:/web/evaluations";
    }
    
    /**
     * Supprime une évaluation
     */
    @PostMapping("/{id}/supprimer")
    public String supprimerEvaluation(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Suppression de l'évaluation {}", id);
        
        try {
            evaluationService.deleteEvaluation(id);
            
            logger.info("Évaluation {} supprimée avec succès", id);
            redirectAttributes.addFlashAttribute("message", "Évaluation supprimée avec succès !");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'évaluation {}", id, e);
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        
        return "redirect:/web/evaluations";
    }
}