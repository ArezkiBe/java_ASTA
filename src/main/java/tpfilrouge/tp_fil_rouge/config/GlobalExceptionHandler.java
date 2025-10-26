package tpfilrouge.tp_fil_rouge.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import tpfilrouge.tp_fil_rouge.exceptions.ApprentiNonTrouveException;
import tpfilrouge.tp_fil_rouge.exceptions.AuthentificationException;
import tpfilrouge.tp_fil_rouge.exceptions.ProgrammeurNonTrouveException;
import tpfilrouge.tp_fil_rouge.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'application
 * Conforme aux exigences ALTN72 - Clean Code - Gestion des exceptions
 *
 * Applique les bonnes pratiques :
 * 1. Catche en priorité les exceptions spécifiques
 * 2. Utilise un framework de logging au lieu de printStackTrace()
 * 3. Messages clairs et explicites en français
 * 4. Exceptions personnalisées avec messages pertinents
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ÉTAPE 1 - Gestion des exceptions spécifiques métier
     * Catché en priorité selon les consignes Clean Code
     */
    @ExceptionHandler(ApprentiNonTrouveException.class)
    public ResponseEntity<Map<String, Object>> handleApprentiNonTrouve(
            ApprentiNonTrouveException ex, WebRequest request) {

        logger.warn("Apprenti non trouvé : {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "Apprenti introuvable",
            ex.getMessage(),
            HttpStatus.NOT_FOUND,
            request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProgrammeurNonTrouveException.class)
    public ResponseEntity<Map<String, Object>> handleProgrammeurNonTrouve(
            ProgrammeurNonTrouveException ex, WebRequest request) {

        logger.warn("Programmeur non trouvé : {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "Programmeur introuvable",
            ex.getMessage(),
            HttpStatus.NOT_FOUND,
            request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AuthentificationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentification(
            AuthentificationException ex, WebRequest request) {

        logger.warn("Erreur d'authentification : {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "Accès non autorisé",
            "Vos informations de connexion sont invalides ou vous n'avez pas les droits nécessaires",
            HttpStatus.UNAUTHORIZED,
            request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * ÉTAPE 2 - Gestion des erreurs de validation
     * Avec messages clairs pour chaque champ en erreur
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        logger.warn("Erreur de validation des données : {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "Données invalides",
            "Les informations saisies ne respectent pas les contraintes requises",
            HttpStatus.BAD_REQUEST,
            request
        );

        // Ajouter les détails des champs en erreur (messages clairs et explicites)
        Map<String, String> champsEnErreur = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String messageExplicite = switch (error.getField()) {
                case "nom" -> "Le nom est obligatoire et doit contenir entre 2 et 100 caractères";
                case "prenom" -> "Le prénom est obligatoire et doit contenir entre 2 et 100 caractères";
                case "email" -> "L'adresse email est obligatoire et doit être valide";
                case "telephone" -> "Le numéro de téléphone doit être valide (10 chiffres)";
                case "programme" -> "Le programme d'études est obligatoire (I1, I2, I3, M2-PRO...)";
                default -> error.getDefaultMessage();
            };
            champsEnErreur.put(error.getField(), messageExplicite);
        });

        errorResponse.put("champs_en_erreur", champsEnErreur);
        errorResponse.put("conseil", "Vérifiez les champs mentionnés ci-dessus et réessayez");

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationMetier(
            ValidationException ex, WebRequest request) {

        logger.warn("Erreur de validation métier : {}", ex.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
            "Règle métier non respectée",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST,
            request
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * ÉTAPE 3 - Gestion des exceptions techniques (Base de données, etc.)
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex, WebRequest request) {

        logger.error("Violation de contrainte de base de données", ex);

        Map<String, Object> errorResponse = createErrorResponse(
            "Erreur de cohérence des données",
            "Cette opération viole une contrainte de base de données (doublons, références manquantes...)",
            HttpStatus.CONFLICT,
            request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * ÉTAPE 4 - Gestion générale des exceptions
     * En dernier recours selon les consignes Clean Code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex, WebRequest request) {

        // Framework de logging au lieu de printStackTrace()
        logger.error("Erreur inattendue dans l'application", ex);

        Map<String, Object> errorResponse = createErrorResponse(
            "Erreur interne du serveur",
            "Une erreur technique inattendue s'est produite. L'équipe technique a été notifiée.",
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Méthode utilitaire pour créer une réponse d'erreur standardisée
     * Messages clairs et explicites selon les consignes du professeur
     */
    private Map<String, Object> createErrorResponse(String titre, String message,
                                                   HttpStatus status, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("horodatage", LocalDateTime.now());
        errorDetails.put("statut", status.value());
        errorDetails.put("erreur", titre);
        errorDetails.put("message", message);
        errorDetails.put("chemin", request.getDescription(false).replace("uri=", ""));

        return errorDetails;
    }
}
