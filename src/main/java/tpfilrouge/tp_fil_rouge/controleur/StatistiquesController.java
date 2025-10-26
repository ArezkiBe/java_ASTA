package tpfilrouge.tp_fil_rouge.controleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tpfilrouge.tp_fil_rouge.modele.repository.ApprentiRepository;

import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
@CrossOrigin(origins = "*")
public class StatistiquesController {

    private final ApprentiRepository apprentiRepository;

    @Autowired
    public StatistiquesController(ApprentiRepository apprentiRepository) {
        this.apprentiRepository = apprentiRepository;
    }

    // Utilise la requête SQL native complexe existante
    @GetMapping("/apprentis-par-programme")
    public ResponseEntity<List<Object[]>> getStatistiquesApprentis() {
        List<Object[]> stats = apprentiRepository.getStatistiquesApprentisParProgrammeEtAnnee();
        return ResponseEntity.ok(stats);
    }
}