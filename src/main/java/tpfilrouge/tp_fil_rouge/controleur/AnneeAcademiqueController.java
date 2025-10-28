package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.services.AnneeAcademiqueService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/annees-academiques")
@CrossOrigin(origins = "*")
public class AnneeAcademiqueController {

    private final AnneeAcademiqueService anneeAcademiqueService;

    @Autowired
    public AnneeAcademiqueController(AnneeAcademiqueService anneeAcademiqueService) {
        this.anneeAcademiqueService = anneeAcademiqueService;
    }

    @GetMapping
    public ResponseEntity<List<AnneeAcademique>> getAllAnnees() {
        List<AnneeAcademique> annees = anneeAcademiqueService.getAllAnnees();
        return ResponseEntity.ok(annees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnneeAcademique> getAnneeById(@PathVariable Integer id) {
        Optional<AnneeAcademique> annee = anneeAcademiqueService.getAnneeById(id);
        return annee.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/courante")
    public ResponseEntity<AnneeAcademique> getAnneeCourante() {
        Optional<AnneeAcademique> annee = anneeAcademiqueService.getAnneeCourante();
        return annee.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnneeAcademique> createAnnee(@RequestBody AnneeAcademique annee) {
        try {
            AnneeAcademique nouvelleAnnee = anneeAcademiqueService.createAnnee(annee);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleAnnee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnneeAcademique> updateAnnee(@PathVariable Integer id, @RequestBody AnneeAcademique anneeDetails) {
        try {
            AnneeAcademique anneeMiseAJour = anneeAcademiqueService.updateAnnee(id, anneeDetails);
            return ResponseEntity.ok(anneeMiseAJour);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ENDPOINT DÉSACTIVÉ POUR SÉCURITÉ
     * La définition manuelle de l'année courante n'est plus autorisée.
     * Utilisez /passer-annee-suivante pour une transition sécurisée.
     */
    @PutMapping("/{id}/definir-courante")
    public ResponseEntity<String> definirAnneeCourante(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("Action non autorisée. Utilisez /passer-annee-suivante pour une progression séquentielle.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnee(@PathVariable Integer id) {
        try {
            anneeAcademiqueService.deleteAnnee(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Gestion des années (Exigence 6)
    @PostMapping("/creer")
    public ResponseEntity<AnneeAcademique> creerNouvelleAnnee(@RequestParam String annee) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(anneeAcademiqueService.creerNouvelleAnnee(annee));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * SEULE MÉTHODE AUTORISÉE pour changer d'année académique
     * Valide automatiquement que la transition est séquentielle (pas de retour, pas de saut)
     */
    @PostMapping("/passer-annee-suivante")
    public ResponseEntity<?> passerAnneeSuivante(@RequestParam String nouvelleAnnee) {
        try {
            AnneeAcademique annee = anneeAcademiqueService.passerAAnneeSuivante(nouvelleAnnee);
            return ResponseEntity.ok(annee);
        } catch (RuntimeException e) {
            // Retourner l'erreur de validation avec détails
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Transition non autorisée",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur interne", "message", e.getMessage()));
        }
    }
}
