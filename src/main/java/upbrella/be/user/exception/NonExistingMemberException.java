package upbrella.be.user.exception;

public class NonExistingMemberException extends RuntimeException {

    public NonExistingMemberException(String message) {

        super(message);
    }
}
