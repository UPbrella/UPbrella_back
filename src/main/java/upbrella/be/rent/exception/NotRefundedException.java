package upbrella.be.rent.exception;

public class NotRefundedException extends RuntimeException{

    public NotRefundedException(String message) {

        super(message);
    }
}
