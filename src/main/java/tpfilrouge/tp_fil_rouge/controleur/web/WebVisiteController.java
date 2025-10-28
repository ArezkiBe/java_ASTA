package tpfilrouge.tp_fil_rouge.controleur.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;
import tpfilrouge.tp_fil_rouge.services.VisiteService;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur web pour la gestion des visites
 */
@Controller
@RequestMapping("/web/visites")
public class WebVisiteController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebVisiteController.class);
    
    private final VisiteService visiteService;
    private final ApprentiService apprentiService;
    
    @Autowired
    public WebVisiteController(VisiteService visiteService, ApprentiService apprentiService) {
        this.visiteService = visiteService;
        this.apprentiService = apprentiService;
    }
    
    /**
     * Affiche la liste des visites
     */
    @GetMapping
    public String listerVisites(Model model) {
        logger.info("Affichage de la liste des visites");
        
        try {
            List<Visite> visites = visiteService.getAllVisites();
            model.addAttribute("visites", visites);
            
            // Statistiques rapides
            long visitesRealisees = visites.stream().filter(v -> "Réalisée".equals(v.getStatut())).count();
            long visitesProgrammees = visites.stream().filter(v -> "Programmée".equals(v.getStatut())).count();
            long visitesAnnulees = visites.stream().filter(v -> "Annulée".equals(v.getStatut())).count();
            
            model.addAttribute("visitesRealisees", visitesRealisees);
            model.addAttribute("visitesProgrammees", visitesProgrammees);
            model.addAttribute("visitesAnnulees", visitesAnnulees);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des visites", e);
            model.addAttribute("erreur", "Erreur lors du chargement des données");
        }
        
        return "visites/liste";
    }
    
    /**
     * Affiche le formulaire de création de visite
     */
    @GetMapping("/nouveau")
    public String afficherFormulaireCreation(Model model) {
        logger.info("Affichage du formulaire de création de visite");
        
        Visite visite = new Visite();
        visite.setStatut("Programmée"); // Statut par défaut
        visite.setDate(LocalDate.now().plusDays(7)); // Date par défaut dans une semaine
        
        model.addAttribute("visite", visite);
        model.addAttribute("modeCreation", true);
        
        // Charger la liste des apprentis actifs
        try {
            List<Apprenti> apprentis = apprentiService.getApprentisCourants();
            model.addAttribute("apprentis", apprentis);
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des apprentis", e);
            model.addAttribute("apprentis", List.of());
        }
        
        return "visites/formulaire";
    }
    
    /**
     * Traite la création d'une visite
     */
    @PostMapping
    public String creerVisite(@ModelAttribute Visite visite, RedirectAttributes redirectAttributes) {
        logger.info("Création d'une nouvelle visite");
        
        try {
            visiteService.createVisite(visite);
            
            logger.info("Visite créée avec succès");
            redirectAttributes.addFlashAttribute("message", "Visite planifiée avec succès !");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la visite", e);
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la planification de la visite");
        }
        
        return "redirect:/web/visites";
    }
    
    /**
     * Affiche le détail d'une visite
     */
    @GetMapping("/{id}")
    public String afficherDetailVisite(@PathVariable Integer id, Model model) {
        logger.info("Affichage du détail de la visite {}", id);
        
        try {
            Visite visite = visiteService.getVisiteById(id)
                .orElseThrow(() -> new RuntimeException("Visite non trouvée"));
            
            model.addAttribute("visite", visite);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la visite {}", id, e);
            model.addAttribute("erreur", "Visite non trouvée");
            return "error/404";
        }
        
        return "visites/details";
    }
    
    /**
     * Affiche le formulaire de modification de visite
     */
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Integer id, Model model) {
        logger.info("Affichage du formulaire de modification de la visite {}", id);
        
        try {
            Visite visite = visiteService.getVisiteById(id)
                .orElseThrow(() -> new RuntimeException("Visite non trouvée"));
            
            model.addAttribute("visite", visite);
            model.addAttribute("modeCreation", false);
            
            // Charger la liste des apprentis actifs
            List<Apprenti> apprentis = apprentiService.getApprentisCourants();
            model.addAttribute("apprentis", apprentis);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la visite {}", id, e);
            model.addAttribute("erreur", "Visite non trouvée");
            return "error/404";
        }
        
        return "visites/formulaire";
    }
    
    /**
     * Traite la modification d'une visite
     */
    @PostMapping("/{id}")
    public String modifierVisite(@PathVariable Integer id, @ModelAttribute Visite visiteDetails, 
                               RedirectAttributes redirectAttributes) {
        logger.info("Modification de la visite {}", id);
        
        try {
            visiteService.updateVisite(id, visiteDetails);
            
            logger.info("Visite {} modifiée avec succès", id);
            redirectAttributes.addFlashAttribute("message", "Visite modifiée avec succès !");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la modification de la visite {}", id, e);
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la modification de la visite");
        }
        
        return "redirect:/web/visites";
    }
    
    /**
     * Supprime une visite
     */
    @PostMapping("/{id}/supprimer")
    public String supprimerVisite(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Suppression de la visite {}", id);
        
        try {
            visiteService.deleteVisite(id);
            
            logger.info("Visite {} supprimée avec succès", id);
            redirectAttributes.addFlashAttribute("message", "Visite supprimée avec succès !");
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la visite {}", id, e);
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la suppression de la visite");
        }
        
        return "redirect:/web/visites";
    }
    
    /**
     * Marque une visite comme réalisée
     */
    @PostMapping("/{id}/marquer-realisee")
    public String marquerVisiteRealisee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Marquage de la visite {} comme réalisée", id);
        
        try {
            Visite visite = visiteService.getVisiteById(id)
                .orElseThrow(() -> new RuntimeException("Visite non trouvée"));
            
            visite.setStatut("Réalisée");
            visiteService.updateVisite(id, visite);
            
            logger.info("Visite {} marquée comme réalisée", id);
            redirectAttributes.addFlashAttribute("message", "Visite marquée comme réalisée !");
            
        } catch (Exception e) {
            logger.error("Erreur lors du marquage de la visite {}", id, e);
            redirectAttributes.addFlashAttribute("erreur", "Erreur lors de la mise à jour du statut");
        }
        
        return "redirect:/web/visites/" + id;
    }
    
    /**
     * Affiche les visites à venir (dans les 30 prochains jours)
     */
    @GetMapping("/prochaines")
    public String afficherVisitesProchaines(Model model) {
        logger.info("Affichage des visites prochaines");
        
        try {
            List<Visite> toutesVisites = visiteService.getAllVisites();
            LocalDate dans30Jours = LocalDate.now().plusDays(30);
            
            List<Visite> visitesProchaines = toutesVisites.stream()
                .filter(v -> "Programmée".equals(v.getStatut()))
                .filter(v -> v.getDate() != null)
                .filter(v -> !v.getDate().isBefore(LocalDate.now()) && !v.getDate().isAfter(dans30Jours))
                .sorted((v1, v2) -> v1.getDate().compareTo(v2.getDate()))
                .toList();
            
            model.addAttribute("visitesProchaines", visitesProchaines);
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des visites prochaines", e);
            model.addAttribute("erreur", "Erreur lors du chargement des données");
        }
        
        return "visites/prochaines";
    }
}