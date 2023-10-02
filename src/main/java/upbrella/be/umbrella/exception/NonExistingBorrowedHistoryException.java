package upbrella.be.umbrella.exception;

public class NonExistingBorrowedHistoryException extends RuntimeException {

    public NonExistingBorrowedHistoryException(String message) {

        super(message);
    }
}
