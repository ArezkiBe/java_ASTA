package tpfilrouge.tp_fil_rouge.exceptions;

public class ProgrammeurNonTrouveException extends IllegalStateException {
    public ProgrammeurNonTrouveException(String messageErreur, Throwable causeException) {
        super(messageErreur, causeException);
    }

    public ProgrammeurNonTrouveException(String messageErreur) {
        super(messageErreur);
    }
}