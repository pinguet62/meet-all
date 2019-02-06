package fr.pinguet62.meetall.exception;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException() {
    }

    public ExpiredTokenException(Throwable cause) {
        super(cause);
    }

}
