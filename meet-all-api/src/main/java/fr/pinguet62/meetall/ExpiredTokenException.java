package fr.pinguet62.meetall;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(Throwable cause) {
        super(cause);
    }

}
