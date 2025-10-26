package tpfilrouge.tp_fil_rouge.exceptions;

/**
 * Exception pour les erreurs de validation métier
 * Conforme aux consignes Clean Code : messages clairs et explicites en français
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String messageErreur, Throwable causeException) {
        super(messageErreur, causeException);
    }

    public ValidationException(String messageErreur) {
        super(messageErreur);
    }

    /**
     * Constructeur pour validation de champ spécifique avec message explicite
     */
    public ValidationException(String champ, String valeur, String regleViolee) {
        super("Validation échouée pour le champ '" + champ +
              "' (valeur: '" + valeur + "'). Règle violée : " + regleViolee);
    }

    /**
     * Constructeur pour validation de règle métier
     */
    public static ValidationException regleMetier(String regle, String explication) {
        return new ValidationException("Règle métier non respectée : " + regle + ". " + explication);
    }
}