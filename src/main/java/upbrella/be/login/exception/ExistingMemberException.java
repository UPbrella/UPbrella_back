package upbrella.be.login.exception;

public class ExistingMemberException extends RuntimeException {

    public ExistingMemberException(String message) {
        super(message);
    }
}
