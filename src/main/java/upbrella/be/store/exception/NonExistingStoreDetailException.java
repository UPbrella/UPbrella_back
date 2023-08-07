package upbrella.be.store.exception;

public class NonExistingStoreDetailException extends RuntimeException {

    public NonExistingStoreDetailException(String message) {
        super(message);
    }
}
