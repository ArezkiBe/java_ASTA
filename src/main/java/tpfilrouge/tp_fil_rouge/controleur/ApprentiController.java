package tpfilrouge.tp_fil_rouge.controleur;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tpfilrouge.tp_fil_rouge.modele.entite.Apprenti;
import tpfilrouge.tp_fil_rouge.services.ApprentiService;

import java.util.List;

@RestController
@RequestMapping("/api/apprentis")
@CrossOrigin(origins = "*")
@Tag(name = "Apprentis", description = "API de gestion des apprentis - CRUD, recherche et gestion des années académiques")
public class ApprentiController {

    private final ApprentiService apprentiService;

    @Autowired
    public ApprentiController(ApprentiService apprentiService) {
        this.apprentiService = apprentiService;
    }

    @Operation(
        summary = "Récupérer tous les apprentis",
        description = "Retourne la liste complète de tous les apprentis (archivés et non-archivés)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des apprentis récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Apprenti.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Apprenti>> getAllApprentis() {
        List<Apprenti> apprentis = apprentiService.getAllApprentis();
        return ResponseEntity.ok(apprentis);
    }

    @Operation(
        summary = "Récupérer un apprenti par son ID",
        description = "Retourne les détails complets d'un apprenti spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apprenti trouvé",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Apprenti.class))),
        @ApiResponse(responseCode = "404", description = "Apprenti non trouvé", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Apprenti> getApprentiById(
            @Parameter(description = "ID unique de l'apprenti", required = true, example = "1")
            @PathVariable Integer id) {
        try {
            Apprenti apprenti = apprentiService.getApprentiById(id);
            return ResponseEntity.ok(apprenti);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Créer un nouvel apprenti",
        description = "Ajoute un nouvel apprenti dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Apprenti créé avec succès",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Apprenti.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Apprenti> createApprenti(
            @Parameter(description = "Données de l'apprenti à créer", required = true)
            @Valid @RequestBody Apprenti apprenti) {
        try {
            Apprenti nouveauApprenti = apprentiService.createApprenti(apprenti);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauApprenti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Mettre à jour un apprenti",
        description = "Modifie tous les champs d'un apprenti existant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apprenti mis à jour avec succès",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Apprenti.class))),
        @ApiResponse(responseCode = "404", description = "Apprenti non trouvé", content = @Content),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Apprenti> updateApprenti(
            @Parameter(description = "ID de l'apprenti à modifier", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Nouvelles données de l'apprenti", required = true)
            @Valid @RequestBody Apprenti apprentiDetails) {
        try {
            Apprenti apprentiMisAJour = apprentiService.updateApprenti(id, apprentiDetails);
            return ResponseEntity.ok(apprentiMisAJour);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Supprimer un apprenti",
        description = "Supprime définitivement un apprenti du système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Apprenti supprimé avec succès", content = @Content),
        @ApiResponse(responseCode = "404", description = "Apprenti non trouvé", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprenti(
            @Parameter(description = "ID de l'apprenti à supprimer", required = true, example = "1")
            @PathVariable Integer id) {
        try {
            apprentiService.deleteApprenti(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Promouvoir les apprentis vers une nouvelle année",
        description = "Archive les apprentis actuels et les fait passer à une nouvelle année académique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion réalisée avec succès",
                    content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "400", description = "Erreur lors de la promotion", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @PostMapping("/promotion/{nouvelleAnneeId}")
    public ResponseEntity<String> promouvoirApprentis(
            @Parameter(description = "ID de la nouvelle année académique", required = true, example = "2")
            @PathVariable Integer nouvelleAnneeId) {
        try {
            String resultat = apprentiService.promouvoirEtArchiverApprentisNouvelleAnnee(nouvelleAnneeId);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la promotion : " + e.getMessage());
        }
    }

    @Operation(
        summary = "Récupérer les apprentis actifs",
        description = "Retourne la liste des apprentis non archivés (actifs pour l'année courante)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des apprentis actifs récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = Apprenti.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
    @GetMapping("/actifs")
    public ResponseEntity<List<Apprenti>> getApprentisCourants() {
        List<Apprenti> apprentis = apprentiService.getAllApprentis()
            .stream()
            .filter(a -> !a.getEstArchive())
            .toList();
        return ResponseEntity.ok(apprentis);
    }
}
