package tpfilrouge.tp_fil_rouge.controleur.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.services.AnneeAcademiqueService;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;

import java.util.List;

@Controller
@RequestMapping("/web/annees")
public class WebAnneeAcademiqueController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebAnneeAcademiqueController.class);
    
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final ApprentiService apprentiService;
    
    @Autowired
    public WebAnneeAcademiqueController(AnneeAcademiqueService anneeAcademiqueService,
                                       ApprentiService apprentiService) {
        this.anneeAcademiqueService = anneeAcademiqueService;
        this.apprentiService = apprentiService;
    }
    
    /**
     * Affiche la page de gestion des années académiques
     */
    @GetMapping
    public String afficherGestionAnnees(Model model) {
        logger.info("Affichage de la page de gestion des années académiques");
        
        try {
            List<AnneeAcademique> annees = anneeAcademiqueService.getAllAnnees();
            AnneeAcademique anneeCourante = anneeAcademiqueService.getAnneeCourante().orElse(null);
            
            model.addAttribute("annees", annees);
            model.addAttribute("anneeCourante", anneeCourante);
            
            // Calcul de l'année suivante pour l'interface
            String anneeSuivante = "";
            if (anneeCourante != null) {
                try {
                    String anneeStr = anneeCourante.getAnnee(); // ex: "2024-2025"
                    int premierAnnee = Integer.parseInt(anneeStr.substring(0, 4)); // 2024
                    anneeSuivante = (premierAnnee + 1) + "-" + (premierAnnee + 2); // "2025-2026"
                } catch (Exception e) {
                    logger.warn("Erreur calcul année suivante pour {}", anneeCourante.getAnnee());
                }
            }
            model.addAttribute("anneeSuivante", anneeSuivante);
            
            // Statistiques des apprentis par programme
            long nombreI1 = apprentiService.compterApprentisByProgramme("I1");
            long nombreI2 = apprentiService.compterApprentisByProgramme("I2");
            long nombreI3 = apprentiService.compterApprentisByProgramme("I3");
            
            model.addAttribute("nombreI1", nombreI1);
            model.addAttribute("nombreI2", nombreI2);
            model.addAttribute("nombreI3", nombreI3);
            
            logger.info("Statistiques chargées : I1={}, I2={}, I3={}", 
                       nombreI1, nombreI2, nombreI3);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des années académiques", e);
            model.addAttribute("erreur", "Erreur lors du chargement des données");
        }
        
        return "annees/gestion";
    }
    
    @PostMapping("/passer-annee-suivante")
    public String passerAnneeSuivante(@RequestParam String nouvelleAnnee, RedirectAttributes redirectAttributes) {
        
        logger.info("🎓 PROMOTION AUTOMATIQUE vers l'année : {}", nouvelleAnnee);
        
        try {
            // La méthode sécurisée valide automatiquement la transition
            anneeAcademiqueService.passerAAnneeSuivante(nouvelleAnnee);
            
            logger.info(" Promotion automatique terminée avec succès vers {}", nouvelleAnnee);
            redirectAttributes.addFlashAttribute("message", 
                " Promotion Automatique Réussie vers " + nouvelleAnnee + " !\n" +
                " Tous les apprentis ont été promus (I1→I2, I2→I3, I3→Diplômés)\n" +
                " Nouvelle année définie comme courante");
            
        } catch (RuntimeException e) {
            // Erreurs de validation (transitions non autorisées)
            logger.warn(" Promotion refusée : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erreur", 
                " " + e.getMessage());
            
        } catch (Exception e) {
            // Erreurs techniques - Log complet pour diagnostic
            logger.error(" Erreur technique lors de la promotion automatique", e);
            redirectAttributes.addFlashAttribute("erreur", 
                " Erreur technique: " + e.getMessage());
        }
        
        return "redirect:/web/annees";
    }
}