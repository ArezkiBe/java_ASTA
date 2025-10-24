package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;

import java.util.List;

@RestController
@RequestMapping("/api/apprentis")
@CrossOrigin(origins = "*")
public class ApprentiController {

    private final ApprentiService apprentiService;

    @Autowired
    public ApprentiController(ApprentiService apprentiService) {
        this.apprentiService = apprentiService;
    }

    @GetMapping
    public ResponseEntity<List<Apprenti>> getAllApprentis() {
        List<Apprenti> apprentis = apprentiService.getAllApprentis();
        return ResponseEntity.ok(apprentis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Apprenti> getApprentiById(@PathVariable Integer id) {
        try {
            Apprenti apprenti = apprentiService.getApprentiById(id);
            return ResponseEntity.ok(apprenti);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Apprenti> createApprenti(@RequestBody Apprenti apprenti) {
        try {
            Apprenti nouveauApprenti = apprentiService.createApprenti(apprenti);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauApprenti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Apprenti> updateApprenti(@PathVariable Integer id, @RequestBody Apprenti apprentiDetails) {
        try {
            Apprenti apprentiMisAJour = apprentiService.updateApprenti(id, apprentiDetails);
            return ResponseEntity.ok(apprentiMisAJour);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprenti(@PathVariable Integer id) {
        try {
            apprentiService.deleteApprenti(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/promotion/{nouvelleAnneeId}")
    public ResponseEntity<String> promouvoirApprentis(@PathVariable Integer nouvelleAnneeId) {
        try {
            String resultat = apprentiService.promouvoirEtArchiverApprentisNouvelleAnnee(nouvelleAnneeId);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la promotion : " + e.getMessage());
        }
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<Apprenti>> getApprentisCourants() {
        List<Apprenti> apprentis = apprentiService.getAllApprentis()
            .stream()
            .filter(a -> !a.getEstArchive())
            .toList();
        return ResponseEntity.ok(apprentis);
    }
}
