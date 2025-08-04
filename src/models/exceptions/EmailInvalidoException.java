package models.exceptions;

public class EmailInvalidoException extends IllegalArgumentException {
    public EmailInvalidoException(String msg) {
        super(msg);
    }
}
