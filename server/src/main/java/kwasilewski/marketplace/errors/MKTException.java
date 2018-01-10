package kwasilewski.marketplace.errors;

public class MKTException extends Exception {

    private static final long serialVersionUID = -5772555765803745207L;
    private final MKTError error;

    public MKTException(MKTError errorCode) {
        super(errorCode.getMessage());
        this.error = errorCode;
    }

    public int getErrorCode() {
        return error.getCode();
    }

    @Override
    public String toString() {
        return error.getCode() + ": " + error.getMessage();
    }

}
