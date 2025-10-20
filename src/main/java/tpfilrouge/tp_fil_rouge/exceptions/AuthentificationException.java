package tpfilrouge.tp_fil_rouge.exceptions;

public class AuthentificationException extends RuntimeException {
    public AuthentificationException(String messageErreur, Throwable causeException) {
        super(messageErreur, causeException);
    }

    public AuthentificationException(String messageErreur) {
        super(messageErreur);
    }
}