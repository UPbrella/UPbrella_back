package upbrella.be.user.exception;

public class InvalidLoginCodeException extends RuntimeException {

    public InvalidLoginCodeException(String message) {

        super(message);
    }

    public InvalidLoginCodeException() {

        super();
    }
}
