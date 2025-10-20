package tpfilrouge.tp_fil_rouge.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String messageErreur, Throwable causeException) {
        super(messageErreur, causeException);
    }

    public ValidationException(String messageErreur) {
        super(messageErreur);
    }
}