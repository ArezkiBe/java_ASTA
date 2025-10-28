package tpfilrouge.tp_fil_rouge.exceptions;

/**
 * Exception personnalisée pour les cas où un maître d'apprentissage n'est pas trouvé
 * Conforme aux consignes Clean Code : messages clairs et explicites
 */
public class MaitreApprentissageNonTrouveException extends RuntimeException {

    public MaitreApprentissageNonTrouveException(String message) {
        super(message);
    }

    public MaitreApprentissageNonTrouveException(Integer id) {
        super("Aucun maître d'apprentissage trouvé avec l'identifiant " + id +
              ". Vérifiez que ce maître existe bien dans la base de données.");
    }

    public MaitreApprentissageNonTrouveException(String field, String value) {
        super("Aucun maître d'apprentissage trouvé avec " + field + " = '" + value + 
              "'. Veuillez vérifier les informations saisies.");
    }
    
    public MaitreApprentissageNonTrouveException(String message, Throwable cause) {
        super(message, cause);
    }
}