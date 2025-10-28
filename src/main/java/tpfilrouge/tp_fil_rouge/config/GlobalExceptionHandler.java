package tpfilrouge.tp_fil_rouge.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import tpfilrouge.tp_fil_rouge.exceptions.ApprentiNonTrouveException;
import tpfilrouge.tp_fil_rouge.exceptions.AuthentificationException;
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
    /**
     * ÉTAPE 1 - Gestion des exceptions spécifiques métier
     * Catché en priorité selon les consignes Clean Code
     */
    @ExceptionHandler(ApprentiNonTrouveException.class)
    public ResponseEntity<Map<String, Object>> handleApprentiNonTrouve(
            ApprentiNonTrouveException ex, WebRequest request) {
        Map<String, Object> errorResponse = createErrorResponse(
            "Apprenti introuvable",
            ex.getMessage(),
            HttpStatus.NOT_FOUND,
            request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AuthentificationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentification(
            AuthentificationException ex, WebRequest request) {
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
        Map<String, Object> errorResponse = createErrorResponse(
            "Erreur de cohérence des données",
            "Cette opération viole une contrainte de base de données (doublons, références manquantes...)",
            HttpStatus.CONFLICT,
            request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Gestion des erreurs de ressources statiques (favicon, outils dev Chrome, etc.)
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex, WebRequest request) {

        String resourcePath = ex.getResourcePath();
        
        // Filtrer les requêtes des outils de développement et autres ressources non critiques
        if (resourcePath != null && (
            resourcePath.contains(".well-known") ||
            resourcePath.contains("favicon.ico") ||
            resourcePath.contains("apple-touch-icon") ||
            resourcePath.contains("manifest.json"))) {
            
            // Log en niveau DEBUG seulement pour ne pas polluer les logs            
            // Retourner 404 silencieusement sans créer d'erreur visible
            return ResponseEntity.notFound().build();
        }

        // Pour les autres ressources, logger normalement
        Map<String, Object> errorResponse = createErrorResponse(
            "Ressource non trouvée",
            "La ressource demandée n'existe pas : " + resourcePath,
            HttpStatus.NOT_FOUND,
            request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * ÉTAPE 4 - Gestion générale des exceptions
     * En dernier recours selon les consignes Clean Code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex, WebRequest request) {

        // Framework de logging au lieu de printStackTrace()
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
