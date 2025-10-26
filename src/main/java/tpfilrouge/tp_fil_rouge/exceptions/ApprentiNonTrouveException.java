package tpfilrouge.tp_fil_rouge.exceptions;

/**
 * Exception personnalisée pour les cas où un apprenti n'est pas trouvé
 * Conforme aux consignes Clean Code : messages clairs et explicites
 */
public class ApprentiNonTrouveException extends RuntimeException {

    public ApprentiNonTrouveException(String message) {
        super(message);
    }

    public ApprentiNonTrouveException(Integer id) {
        super("Aucun apprenti trouvé avec l'identifiant " + id +
              ". Vérifiez que cet apprenti existe bien dans la base de données.");
    }

    public ApprentiNonTrouveException(String critere, String valeur) {
        super("Aucun apprenti trouvé pour le critère '" + critere +
              "' avec la valeur '" + valeur + "'. Vérifiez vos paramètres de recherche.");
    }
}

