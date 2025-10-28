package tpfilrouge.tp_fil_rouge.controleur.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.modele.entite.Entreprise;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;
import tpfilrouge.tp_fil_rouge.services.AnneeAcademiqueService;
import tpfilrouge.tp_fil_rouge.services.EntrepriseService;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur web pour la recherche d'apprentis
 * Exigence #7 : Interface de recherche par critères multiples
 */
@Controller
@RequestMapping("/web/recherche")
public class WebRechercheController {

    private static final Logger logger = LoggerFactory.getLogger(WebRechercheController.class);

    private final ApprentiService apprentiService;
    private final EntrepriseService entrepriseService;
    private final AnneeAcademiqueService anneeAcademiqueService;

    @Autowired
    public WebRechercheController(ApprentiService apprentiService,
                                EntrepriseService entrepriseService,
                                AnneeAcademiqueService anneeAcademiqueService) {
        this.apprentiService = apprentiService;
        this.entrepriseService = entrepriseService;
        this.anneeAcademiqueService = anneeAcademiqueService;
    }

    /**
     * Affiche la page de recherche avec les formulaires
     */
    @GetMapping
    public String afficherPageRecherche(Model model) {
        logger.info("Affichage de la page de recherche d'apprentis");
        
        try {
            // Charger les données pour les dropdowns
            List<Entreprise> entreprises = entrepriseService.getAllEntreprises();
            List<AnneeAcademique> annees = anneeAcademiqueService.getAllAnnees();
            
            model.addAttribute("entreprises", entreprises);
            model.addAttribute("annees", annees);
            model.addAttribute("resultats", new ArrayList<Apprenti>());
            model.addAttribute("rechercheEffectuee", false);
            
            logger.info("Page de recherche chargée avec {} entreprises et {} années", 
                       entreprises.size(), annees.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de la page de recherche", e);
            model.addAttribute("erreur", "Erreur lors du chargement des données");
        }
        
        return "recherche/page";
    }

    /**
     * Recherche par nom d'apprenti (Exigence 7.1.1)
     */
    @PostMapping("/nom")
    public String rechercherParNom(@RequestParam String nom, 
                                  Model model, 
                                  RedirectAttributes redirectAttributes) {
        
        logger.info("Recherche par nom : '{}'", nom);
        
        if (nom == null || nom.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Le nom de recherche ne peut pas être vide");
            return "redirect:/web/recherche";
        }
        
        try {
            List<Apprenti> resultats = apprentiService.rechercherParNom(nom.trim());
            
            // Recharger les données pour la page
            model.addAttribute("entreprises", entrepriseService.getAllEntreprises());
            model.addAttribute("annees", anneeAcademiqueService.getAllAnnees());
            model.addAttribute("resultats", resultats);
            model.addAttribute("rechercheEffectuee", true);
            model.addAttribute("typeRecherche", "nom");
            model.addAttribute("critereRecherche", nom.trim());
            model.addAttribute("nombreResultats", resultats.size());
            
            logger.info("Recherche par nom '{}' : {} résultat(s)", nom, resultats.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche par nom '{}'", nom, e);
            model.addAttribute("erreur", "Erreur lors de la recherche");
            return afficherPageRecherche(model);
        }
        
        return "recherche/page";
    }

    /**
     * Recherche par entreprise (Exigence 7.1.2)
     */
    @PostMapping("/entreprise")
    public String rechercherParEntreprise(@RequestParam Integer entrepriseId, 
                                        Model model, 
                                        RedirectAttributes redirectAttributes) {
        
        logger.info("Recherche par entreprise ID : {}", entrepriseId);
        
        if (entrepriseId == null) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez sélectionner une entreprise");
            return "redirect:/web/recherche";
        }
        
        try {
            List<Apprenti> resultats = apprentiService.rechercherParEntreprise(entrepriseId);
            Entreprise entreprise = entrepriseService.getEntrepriseById(entrepriseId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
            
            // Recharger les données pour la page
            model.addAttribute("entreprises", entrepriseService.getAllEntreprises());
            model.addAttribute("annees", anneeAcademiqueService.getAllAnnees());
            model.addAttribute("resultats", resultats);
            model.addAttribute("rechercheEffectuee", true);
            model.addAttribute("typeRecherche", "entreprise");
            model.addAttribute("critereRecherche", entreprise.getRaisonSociale());
            model.addAttribute("nombreResultats", resultats.size());
            
            logger.info("Recherche par entreprise '{}' : {} résultat(s)", 
                       entreprise.getRaisonSociale(), resultats.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche par entreprise ID {}", entrepriseId, e);
            model.addAttribute("erreur", "Erreur lors de la recherche");
            return afficherPageRecherche(model);
        }
        
        return "recherche/page";
    }

    /**
     * Recherche par mission/mots-clés (Exigence 7.1.3)
     */
    @PostMapping("/mission")
    public String rechercherParMission(@RequestParam String motsCles, 
                                     Model model, 
                                     RedirectAttributes redirectAttributes) {
        
        logger.info("Recherche par mission : '{}'", motsCles);
        
        if (motsCles == null || motsCles.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Les mots-clés ne peuvent pas être vides");
            return "redirect:/web/recherche";
        }
        
        try {
            List<Apprenti> resultats = apprentiService.rechercherParMission(motsCles.trim());
            
            // Recharger les données pour la page
            model.addAttribute("entreprises", entrepriseService.getAllEntreprises());
            model.addAttribute("annees", anneeAcademiqueService.getAllAnnees());
            model.addAttribute("resultats", resultats);
            model.addAttribute("rechercheEffectuee", true);
            model.addAttribute("typeRecherche", "mission");
            model.addAttribute("critereRecherche", motsCles.trim());
            model.addAttribute("nombreResultats", resultats.size());
            
            logger.info("Recherche par mission '{}' : {} résultat(s)", motsCles, resultats.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche par mission '{}'", motsCles, e);
            model.addAttribute("erreur", "Erreur lors de la recherche");
            return afficherPageRecherche(model);
        }
        
        return "recherche/page";
    }

    /**
     * Recherche par année académique (Exigence 7.1.4)
     */
    @PostMapping("/annee")
    public String rechercherParAnnee(@RequestParam String annee, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {
        
        logger.info("Recherche par année : '{}'", annee);
        
        if (annee == null || annee.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez sélectionner une année");
            return "redirect:/web/recherche";
        }
        
        try {
            List<Apprenti> resultats = apprentiService.rechercherParAnnee(annee.trim());
            
            // Recharger les données pour la page
            model.addAttribute("entreprises", entrepriseService.getAllEntreprises());
            model.addAttribute("annees", anneeAcademiqueService.getAllAnnees());
            model.addAttribute("resultats", resultats);
            model.addAttribute("rechercheEffectuee", true);
            model.addAttribute("typeRecherche", "annee");
            model.addAttribute("critereRecherche", annee.trim());
            model.addAttribute("nombreResultats", resultats.size());
            
            logger.info("Recherche par année '{}' : {} résultat(s)", annee, resultats.size());
            
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche par année '{}'", annee, e);
            model.addAttribute("erreur", "Erreur lors de la recherche");
            return afficherPageRecherche(model);
        }
        
        return "recherche/page";
    }

    /**
     * Réinitialise la recherche
     */
    @PostMapping("/reset")
    public String reinitialiserRecherche() {
        logger.info("Réinitialisation de la recherche");
        return "redirect:/web/recherche";
    }
}