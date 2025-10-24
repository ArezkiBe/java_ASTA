package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.entite.Entreprise;
import tpfilrouge.tp_fil_rouge.services.EntrepriseService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entreprises")
@CrossOrigin(origins = "*")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    @Autowired
    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @GetMapping
    public ResponseEntity<List<Entreprise>> getAllEntreprises() {
        List<Entreprise> entreprises = entrepriseService.getAllEntreprises();
        return ResponseEntity.ok(entreprises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> getEntrepriseById(@PathVariable Integer id) {
        Optional<Entreprise> entreprise = entrepriseService.getEntrepriseById(id);
        return entreprise.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Entreprise> createEntreprise(@RequestBody Entreprise entreprise) {
        try {
            Entreprise nouvelleEntreprise = entrepriseService.createEntreprise(entreprise);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleEntreprise);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entreprise> updateEntreprise(@PathVariable Integer id, @RequestBody Entreprise entrepriseDetails) {
        try {
            Entreprise entrepriseMiseAJour = entrepriseService.updateEntreprise(id, entrepriseDetails);
            return ResponseEntity.ok(entrepriseMiseAJour);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Integer id) {
        try {
            entrepriseService.deleteEntreprise(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
