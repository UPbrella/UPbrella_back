package upbrella.be.user.exception;

public class NotSocialLoginedException extends RuntimeException {

    public NotSocialLoginedException(String message) {

        super(message);
    }

    public NotSocialLoginedException() {

        super();
    }
}
