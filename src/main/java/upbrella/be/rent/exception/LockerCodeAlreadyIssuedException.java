package upbrella.be.rent.exception;

public class LockerCodeAlreadyIssuedException extends RuntimeException {

    public LockerCodeAlreadyIssuedException(String message) {

        super(message);
    }
}
