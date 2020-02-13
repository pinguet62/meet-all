package fr.pinguet62.meetall.exception;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(Throwable cause) {
        super(cause);
    }

}
