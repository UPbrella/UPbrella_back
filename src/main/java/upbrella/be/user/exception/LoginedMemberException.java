package upbrella.be.user.exception;

public class LoginedMemberException extends RuntimeException {

    public LoginedMemberException(String message) {

        super(message);
    }
}
