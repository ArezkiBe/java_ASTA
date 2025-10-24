package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.entite.Visite;
import tpfilrouge.tp_fil_rouge.services.VisiteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/visites")
@CrossOrigin(origins = "*")
public class VisiteController {

    private final VisiteService visiteService;

    @Autowired
    public VisiteController(VisiteService visiteService) {
        this.visiteService = visiteService;
    }

    @GetMapping
    public ResponseEntity<List<Visite>> getAllVisites() {
        List<Visite> visites = visiteService.getAllVisites();
        return ResponseEntity.ok(visites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visite> getVisiteById(@PathVariable Integer id) {
        Optional<Visite> visite = visiteService.getVisiteById(id);
        return visite.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Visite> createVisite(@RequestBody Visite visite) {
        try {
            Visite nouvelleVisite = visiteService.createVisite(visite);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleVisite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visite> updateVisite(@PathVariable Integer id, @RequestBody Visite visiteDetails) {
        try {
            Visite visiteMiseAJour = visiteService.updateVisite(id, visiteDetails);
            return ResponseEntity.ok(visiteMiseAJour);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisite(@PathVariable Integer id) {
        try {
            visiteService.deleteVisite(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
