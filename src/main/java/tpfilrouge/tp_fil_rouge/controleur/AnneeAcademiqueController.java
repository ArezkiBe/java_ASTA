package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.entite.AnneeAcademique;
import tpfilrouge.tp_fil_rouge.services.AnneeAcademiqueService;

import java.util.List;
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

    @PutMapping("/{id}/definir-courante")
    public ResponseEntity<AnneeAcademique> definirAnneeCourante(@PathVariable Integer id) {
        try {
            AnneeAcademique annee = anneeAcademiqueService.definirNouvelleAnneeCourante(id);
            return ResponseEntity.ok(annee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
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
}
